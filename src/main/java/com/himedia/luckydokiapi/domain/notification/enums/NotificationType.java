package com.himedia.luckydokiapi.domain.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    SELLER_APPROVAL("셀러 승인"),
    COUPON_ISSUE("쿠폰 발급"),
    COUPON_EXPIRATION("쿠폰 만료"),
    WELCOME("회원가입 축하"),
    ;

    private final String description;
}
