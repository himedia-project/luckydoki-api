package com.himedia.luckydokiapi.domain.coupon.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssueRequestDTO {

    private List<String> emails;

    private String code;        // 해당 유저가 등록하는 코드

}
