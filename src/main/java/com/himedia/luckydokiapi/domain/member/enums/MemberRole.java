package com.himedia.luckydokiapi.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    USER("유저"), SELLER("셀러"), ADMIN("관리자");

    private final String roleName;
}//0      1
