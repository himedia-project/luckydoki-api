package com.himedia.luckydokiapi.domain.payment.service;


import com.himedia.luckydokiapi.domain.cart.repository.CartRepository;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.email.service.EmailService;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.service.OrderService;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentCancelDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentPrepareDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentResponseDTO;
import com.himedia.luckydokiapi.domain.payment.entity.Payment;
import com.himedia.luckydokiapi.domain.payment.enums.PaymentStatus;
import com.himedia.luckydokiapi.domain.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    @Value("${toss.secret-key}")
    private String secretKey;

    @Value(("${toss.url}"))
    private String tossUrl;

    private final RestTemplate restTemplate;

    private final EmailService emailService;
    private final PaymentRepository paymentRepository;

    private final OrderService orderService;

    private final CouponService couponService;

    private final CartRepository cartRepository;

    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;

    @Override
    public void preparePayment(PaymentPrepareDTO dto) {
        log.info("PaymentService preparePayment...");
        Order order = orderService.getEntityByCode(dto.getOrderId());

        Payment payment = Payment.builder()
                .order(order)
                .amount(dto.getAmount())
                .status(PaymentStatus.READY)
                .build();

        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    @Override
    public void validatePayment(String orderId, Long amount) {
        log.info("PaymentService validatePayment...");
        Payment payment = getPayment(orderId);

        if (!Objects.equals(payment.getAmount(), amount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

    @Override
    public PaymentResponseDTO confirmPayment(String paymentKey, String orderId, Long amount) {
        log.info("PaymentService confirmPayment... paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);

        // Toss Payments API 요청 및 결제 확인
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderId);
        params.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);
        String confirmUrl = tossUrl + "/payments/confirm";

        try {
            ResponseEntity<PaymentResponseDTO> response = restTemplate.postForEntity(
                    confirmUrl, request, PaymentResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Payment payment = getPayment(orderId);
                payment.setPaymentKey(paymentKey);
                payment.setStatus(PaymentStatus.DONE);
                payment.setMethod(Objects.requireNonNull(response.getBody()).getMethod());
                payment.setRequestedAt(Objects.requireNonNull(response.getBody()).getRequestedAt().toLocalDateTime());
                payment.setApprovedAt(Objects.requireNonNull(response.getBody()).getApprovedAt().toLocalDateTime());
                paymentRepository.save(payment);

                // 주문 상태 변경 (동기 처리)
                Order order = orderService.getEntityByCode(orderId);
                order.changeStatusToConfirm();

                /**
                 * ThreadPoolTaskExecutor 사용으로 개선:
                 * - 스프링 관리형 스레드 풀 사용
                 * - 각 비동기 작업을 개별적으로 제출
                 * - 예외 처리가 각 작업 내부에서 이루어짐
                 * - 애플리케이션 종료 시 스프링이 자동으로 정리
                 */
                
                // 쿠폰 사용 처리 (비동기)
                taskExecutor.execute(() -> {
                    try {
                        if (order.getCoupon() != null) {
                            couponService.useCoupon(order.getMember().getEmail(), order.getCoupon());
                            log.debug("쿠폰 사용 처리 완료: {}", order.getCoupon().getId());
                        }
                    } catch (Exception e) {
                        log.error("쿠폰 사용 처리 중 오류 발생: {}", e.getMessage(), e);
                    }
                });

                // 장바구니 비우기 (비동기)
                taskExecutor.execute(() -> {
                    try {
                        cartRepository.getCartOfMember(order.getMember().getEmail())
                                .ifPresent(cart -> orderService.removeCartItemsMatchedOrderItemsBy(cart, order.getOrderItems()));
                        log.debug("장바구니 비우기 완료: {}", order.getMember().getEmail());
                    } catch (Exception e) {
                        log.error("장바구니 비우기 중 오류 발생: {}", e.getMessage(), e);
                    }
                });

                // 이메일 전송 (비동기)
                taskExecutor.execute(() -> {
                    try {
                        String userEmail = order.getMember().getEmail();
                        emailService.sendPaymentConfirmation(userEmail, orderId, amount.toString());
                        log.debug("주문 확인 이메일 전송 완료: {}", userEmail);
                    } catch (Exception e) {
                        log.error("이메일 전송 중 오류 발생: {}", e.getMessage(), e);
                    }
                });

                log.info("결제 확인 성공: {}", response.getBody());
                return response.getBody();
            }

        } catch (RestClientException e) {
            log.error("결제 승인 실패", e);
            throw new RuntimeException("결제 승인 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    @Override
    public PaymentResponseDTO cancelPayment(String orderId, PaymentCancelDTO cancelDTO) {
        Payment payment = getPayment(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("cancelReason", cancelDTO.getCancelReason());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        PaymentResponseDTO response = restTemplate.postForObject(
                tossUrl + "/" + payment.getPaymentKey() + "/cancel",
                request,
                PaymentResponseDTO.class
        );

        payment.setStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);

        return response;
    }

    private Payment getPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 정보가 없습니다. orderId(code): " + orderId));
    }
}
