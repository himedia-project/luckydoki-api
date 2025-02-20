package com.himedia.luckydokiapi.domain.coupon.dto;

import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CouponResponseDTO {
	private Long id;
	private String code;
	private String name;
	private String content;
	private Integer minimumUsageAmount;
	private Integer discountPrice;
	private LocalDate startDate;
	private LocalDate endDate;
	private CouponStatus status;


	public static CouponResponseDTO from(Coupon coupon) {
		return CouponResponseDTO.builder()
				.id(coupon.getId())
				.code(coupon.getCode())
				.name(coupon.getName())
				.content(coupon.getContent())
				.minimumUsageAmount(coupon.getMinimumUsageAmount())
				.discountPrice(coupon.getDiscountPrice())
				.startDate(coupon.getStartDate())
				.endDate(coupon.getEndDate())
				.status(coupon.getStatus())
				.build();
	}
}
