package com.himedia.luckydokiapi.domain.payment.controller;


import com.himedia.luckydokiapi.domain.payment.dto.PaymentCancelDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentConfirmDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentPrepareDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentResponseDTO;
import com.himedia.luckydokiapi.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 정보 임시 저장
    @PostMapping("/prepare")
    public ResponseEntity<?> preparePayment(@RequestBody PaymentPrepareDTO dto) {
        log.info("preparePayment dto: {}", dto);
        paymentService.preparePayment(dto);
        return ResponseEntity.ok("payment prepare success");
    }


    // 결제 금액 검증 API
    @GetMapping("/validate/{orderId}")
    public ResponseEntity<String> validatePayment(@PathVariable String orderId, @RequestParam Long amount) {
        log.info("validatePayment orderId: {}, amount: {}", orderId, amount);
        paymentService.validatePayment(orderId, amount);

        return ResponseEntity.ok("payment amount matched");
    }

    // 결제 승인 요청 API
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(
            @RequestBody PaymentConfirmDTO dto
    ) {
        log.info("confirmPayment dto: {}", dto);
        return ResponseEntity.ok(paymentService.confirmPayment(dto.getPaymentKey(), dto.getOrderId(), dto.getAmount()));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<PaymentResponseDTO> cancelPayment(
            @PathVariable String orderId,
            @RequestBody PaymentCancelDTO cancelDTO) {
        log.info("cancelPayment orderId: {}, cancelDTO: {}", orderId, cancelDTO);
        return ResponseEntity.ok(paymentService.cancelPayment(orderId, cancelDTO));
    }


}
