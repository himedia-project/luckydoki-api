package com.himedia.luckydokiapi.domain.payment.dto;


import com.himedia.luckydokiapi.domain.payment.entity.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentPrepareDTO {

    private String orderId;
    private Long amount;

    public Payment toEntity() {
        return Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
