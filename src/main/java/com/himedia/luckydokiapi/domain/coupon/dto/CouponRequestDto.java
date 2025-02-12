package com.himedia.luckydokiapi.domain.coupon.dto;

import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class CouponRequestDto extends PageRequestDTO {

	private String name;
	private String content;
	private CouponStatus status;

	// 검색 키워드
	private String searchKeyword;
}
