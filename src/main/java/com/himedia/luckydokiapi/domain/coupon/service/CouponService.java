package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface CouponService {
	PageResponseDTO<CouponResponseDTO> getAllCoupons(CouponRequestDto requestDto);
	List<CouponResponseDTO> getActiveCoupons();
	CouponResponseDTO getCouponById(Long id);
	CouponResponseDTO getCouponByCode(String code);
	Long createCoupon(CouponRequestDto couponRequestDto);
	CouponResponseDTO updateCoupon(Long id, CouponRequestDto couponRequestDto);
	void deleteCoupon(Long id);

	PageResponseDTO<CouponRecordResponseDTO> getCouponRecords(CouponRecordSearchDTO requestDto);

	void issueCoupon(Long couponId, List<String> memberEmails);

	Coupon getCoupon(Long couponId);

	List<CouponResponseDTO> getCouponList(String email);

	int countAllCoupon();

	void createWelcomeCoupon();

    void useCoupon(String email, Coupon coupon);

    String requestMassIssue(Long couponId, List<String> emails);

	String getIssueStatus(String requestId);

	void processMassIssue(Long couponId, List<String> emails, String requestId);

}