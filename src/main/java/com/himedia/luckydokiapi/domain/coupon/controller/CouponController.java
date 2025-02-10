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
	
	@GetMapping
	public List<CouponDto> getAllCoupons() {
		return couponService.getAllCoupons();
	}
	
	@GetMapping("/active")
	public List<CouponDto> getActiveCoupons() {
		return couponService.getActiveCoupons();
	}
	
	@GetMapping("/{id}")
	public CouponDto getCouponById(@PathVariable Long id) {
		return couponService.getCouponById(id);
	}
	
}
