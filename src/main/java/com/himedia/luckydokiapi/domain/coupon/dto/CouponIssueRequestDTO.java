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

}
