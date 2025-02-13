package com.himedia.luckydokiapi.domain.coupon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponRecordStatus {

    USED("사용됨"),
    UNUSED("미사용"),
    EXPIRED("만료됨");

    private final String description;
}
