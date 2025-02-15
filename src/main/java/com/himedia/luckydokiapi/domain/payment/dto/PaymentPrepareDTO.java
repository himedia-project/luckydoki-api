package com.himedia.luckydokiapi.domain.payment.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentPrepareDTO {

    private String orderId;
    private Long amount;

}
