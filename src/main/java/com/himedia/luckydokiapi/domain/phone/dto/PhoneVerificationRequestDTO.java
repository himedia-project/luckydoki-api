package com.himedia.luckydokiapi.domain.phone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneVerificationRequestDTO {
    private String phone;
    private String code; // verify 요청 시 필요
}
