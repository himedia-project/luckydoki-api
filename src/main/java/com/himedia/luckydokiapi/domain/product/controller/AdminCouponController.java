package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/coupon")
public class AdminCouponController {
	private CouponService couponService;
	
	public AdminCouponController(CouponService couponService) {
		this.couponService = couponService;
	}
	
	@PostMapping
	public CouponDto createCoupon(@RequestBody CouponDto couponDto) {
		return couponService.createCoupon(couponDto);
	}
	
	@PutMapping("/{id}")
	public CouponDto updateCoupon(@PathVariable Long id, @RequestBody CouponDto couponDto) {
		return couponService.updateCoupon(id, couponDto);
	}
	
	@DeleteMapping("/{id}")
	public void deleteCoupon(@PathVariable Long id) {
		couponService.deleteCoupon(id);
	}
}
