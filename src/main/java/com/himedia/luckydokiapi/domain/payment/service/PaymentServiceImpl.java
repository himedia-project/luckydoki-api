package com.himedia.luckydokiapi.domain.payment.service;


import com.himedia.luckydokiapi.domain.cart.entity.Cart;
import com.himedia.luckydokiapi.domain.cart.repository.CartRepository;
import com.himedia.luckydokiapi.domain.cart.service.CartService;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
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
import org.springframework.beans.factory.annotation.Value;
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

    private final CartService cartService;
    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate;

    private static final String TOSS_URL = "https://api.tosspayments.com/v1";

    private final PaymentRepository paymentRepository;

    private final OrderService orderService;

    private final CouponService couponService;

    private final CartRepository cartRepository;

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
        log.info("PaymentService confirmPayment... paymentKey: {}, orderId: {}, amount: {}",
                paymentKey, orderId, amount);
        // 시크릿 키를 Base64로 인코딩
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderId);
        params.put("amount", amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        // URL 변경
        String confirmUrl = TOSS_URL + "/payments/confirm";

        log.info("Toss Payment API request URL: {}", confirmUrl);
        log.info("Toss Payment API request headers: {}", headers);
        log.info("Toss Payment API request body: {}", params);

        try {
            ResponseEntity<PaymentResponseDTO> response = restTemplate.postForEntity(
                    confirmUrl,
                    request,
                    PaymentResponseDTO.class
            );

            // 성공 시에만 DB 업데이트
            if (response.getStatusCode() == HttpStatus.OK) {
                Payment payment = getPayment(orderId);
                payment.setPaymentKey(paymentKey);
                payment.setStatus(PaymentStatus.DONE);
                payment.setMethod(Objects.requireNonNull(response.getBody()).getMethod());
                payment.setRequestedAt(Objects.requireNonNull(response.getBody()).getRequestedAt().toLocalDateTime());
                payment.setApprovedAt(Objects.requireNonNull(response.getBody()).getApprovedAt().toLocalDateTime());
                paymentRepository.save(payment);

                // 주문상태, 결제완료 처리
                Order order = orderService.getEntityByCode(orderId);
                Cart cart = cartRepository.getCartOfMember(order.getMember().getEmail())
                        .orElseThrow(() -> new EntityNotFoundException("해당 회원의 장바구니가 없습니다. email: " + order.getMember().getEmail()));
                order.changeStatusToConfirm();
                // TODO: 잠시 보류 -> 주문 데이터 추가시, 주석풀기
                // 쿠폰 사용시, 쿠폰 사용 처리
//                if (order.getCoupon() != null) {
//                    couponService.useCoupon(order.getMember().getEmail(), order.getCoupon());
//                }
                // 장바구니 상품 삭제
                orderService.removeCartItemsMatchedOrderItemsBy(cart, order.getOrderItems());

                log.info("Payment confirmation successful: {}", response.getBody());
            }

            return response.getBody();
        } catch (RestClientException e) {
            log.error("Payment confirmation failed", e);
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
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
                TOSS_URL + "/" + payment.getPaymentKey() + "/cancel",
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
