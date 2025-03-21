package com.himedia.luckydokiapi.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponMassIssueRequestDTO {
    private Long couponId;
    private List<String> emails;
    private String requestId;  // 중복 요청 방지용 고유 ID
} 