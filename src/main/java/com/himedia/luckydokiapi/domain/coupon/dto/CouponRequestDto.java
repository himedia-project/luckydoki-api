package com.himedia.luckydokiapi.domain.coupon.dto;

import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CouponRequestDto {
	private String code;
	private String name;
	private String content;
	private CouponStatus status;
}
