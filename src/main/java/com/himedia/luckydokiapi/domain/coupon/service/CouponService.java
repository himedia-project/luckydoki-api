package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;

import java.util.List;

public interface CouponService {
	List<CouponResponseDto> getAllCoupons();
	List<CouponResponseDto> getActiveCoupons();
	CouponResponseDto getCouponById(Long id);
	CouponResponseDto getCouponByCode(String code);
	CouponResponseDto createCoupon(CouponRequestDto couponRequestDto);
	CouponResponseDto updateCoupon(Long id, CouponRequestDto couponRequestDto);
	void deleteCoupon(Long id);
}