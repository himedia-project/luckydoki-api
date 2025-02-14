package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface CouponService {
	PageResponseDTO<CouponResponseDto> getAllCoupons(CouponRequestDto requestDto);
	List<CouponResponseDto> getActiveCoupons();
	CouponResponseDto getCouponById(Long id);
	CouponResponseDto getCouponByCode(String code);
	Long createCoupon(CouponRequestDto couponRequestDto);
	CouponResponseDto updateCoupon(Long id, CouponRequestDto couponRequestDto);
	void deleteCoupon(Long id);

	PageResponseDTO<CouponRecordResponseDTO> getCouponRecords(CouponRecordSearchDTO requestDto);

	void issueCoupon(Long couponId, List<String> memberEmails);

	Coupon getCoupon(Long couponId);

	List<CouponResponseDto> getCouponList(String email);
}