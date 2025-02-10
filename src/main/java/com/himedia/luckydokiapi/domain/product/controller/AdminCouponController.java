package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {
	private CouponService couponService;
	
	// 모든 쿠폰 목록 조회
	@GetMapping
	public List<CouponDto> getAllCoupons() {
		return couponService.getAllCoupons();
	}
	
	// 새로운 쿠폰 생성
	@PostMapping
	public CouponDto createCoupon(@RequestBody CouponDto couponDto) {
		return couponService.createCoupon(couponDto);
	}
	
	// 쿠폰아이디로 쿠폰 조회
	@GetMapping("/{id}")
	public CouponDto getCouponById(@PathVariable Long id) {
		return couponService.getCouponById(id);
	}
	
	// 쿠폰 정보 수정
	@PutMapping("/{id}")
	public CouponDto updateCoupon(@PathVariable Long id, @RequestBody CouponDto couponDto) {
		return couponService.updateCoupon(id, couponDto);
	}
	
	// 쿠폰 삭제
	@DeleteMapping("/{id}")
	public void deleteCoupon(@PathVariable Long id) {
		couponService.deleteCoupon(id);
	}
}
