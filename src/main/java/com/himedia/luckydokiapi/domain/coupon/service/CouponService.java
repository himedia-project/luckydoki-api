package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;

import java.util.List;

public interface CouponService {
	List<CouponDto> getAllCoupons();
	List<CouponDto> getActiveCoupons();
	CouponDto getCouponById(Long id);
	CouponDto createCoupon(CouponDto couponDto);
	CouponDto updateCoupon(Long id, CouponDto couponDto);
	void deleteCoupon(Long id);
}