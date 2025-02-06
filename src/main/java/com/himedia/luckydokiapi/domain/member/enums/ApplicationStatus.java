package com.himedia.luckydokiapi.domain.member.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("승인 대기"),
    APPROVED("승인 완료"),
    REJECTED("거절됨");

    private final String description; // 한글 설명 추가

    ApplicationStatus(String description) {
        this.description = description;
    }
}
