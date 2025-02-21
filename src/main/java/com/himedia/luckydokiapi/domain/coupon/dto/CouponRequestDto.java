package com.himedia.luckydokiapi.domain.coupon.dto;

import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.dto.PageRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class CouponRequestDto extends PageRequestDTO {

	@NotBlank(message = "쿠폰 이름은 필수입니다.")
	private String name;
	@NotBlank(message = "쿠폰 내용은 필수입니다.")
	private String content;
	private CouponStatus status;

	private Integer discountPrice;
	private Integer minimumUsageAmount;

	private LocalDate startDate;
	private LocalDate endDate;

	// 검색 키워드
	private String searchKeyword;
}
