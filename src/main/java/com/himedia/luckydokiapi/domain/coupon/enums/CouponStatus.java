package com.himedia.luckydokiapi.domain.coupon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {

    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    ISSUED("발급"),
    EXPIRED("만료");


    private final String description;
}
