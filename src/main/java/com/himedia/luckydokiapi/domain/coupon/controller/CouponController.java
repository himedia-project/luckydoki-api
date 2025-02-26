package com.himedia.luckydokiapi.domain.coupon.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@Tag(name = "coupon-api", description ="쿠폰 코드로 사용가능한 쿠폰을 조회 하는 api ")
public class CouponController {
	private final CouponService couponService;

	// 쿠폰 코드로 조회
	@GetMapping("/{code}")
	public CouponResponseDTO getCouponByCode(@Parameter(description = "쿠폰 코드 번호")@PathVariable String code) {
		
		log.info("getCouponByCode: {}", code);
		return couponService.getCouponByCode(code);
	}


	
	// 나머지 CRUD 는 AdminCouponController 에서 구현
}
