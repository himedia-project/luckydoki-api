package com.himedia.luckydokiapi.domain.coupon.controller;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
	private final CouponService couponService;
	
	// 쿠폰 코드로 조회
	@GetMapping("/{code}")
	public CouponDto getCouponByCode(@PathVariable String code) {
		return couponService.getCouponByCode(code);
	}
	
	
	// 나머지 CRUD 는 AdminCouponController 에서 구현
}
