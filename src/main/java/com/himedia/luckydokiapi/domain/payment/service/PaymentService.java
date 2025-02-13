package com.himedia.luckydokiapi.domain.payment.service;


import com.himedia.luckydokiapi.domain.payment.dto.PaymentCancelDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentPrepareDTO;
import com.himedia.luckydokiapi.domain.payment.dto.PaymentResponseDTO;

public interface PaymentService {
    void preparePayment(PaymentPrepareDTO dto);

    void validatePayment(String orderId, Long amount);

    PaymentResponseDTO confirmPayment(String paymentKey, String orderId, Long amount);

    PaymentResponseDTO cancelPayment(String orderId, PaymentCancelDTO cancelDTO);
}
