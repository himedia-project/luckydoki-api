package com.himedia.luckydokiapi.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    ORDER("주문중"), CONFIRM("결제완료"), CANCEL("주문취소");

    private final String description;
}
