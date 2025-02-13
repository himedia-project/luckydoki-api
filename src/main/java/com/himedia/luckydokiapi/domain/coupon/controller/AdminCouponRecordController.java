package com.himedia.luckydokiapi.domain.coupon.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/coupon-record")
@RequiredArgsConstructor
public class AdminCouponRecordController {
	private final CouponService couponService;

	// 모든 쿠폰 목록 조회
	@GetMapping("/list")
	public ResponseEntity<PageResponseDTO<CouponRecordResponseDTO>> getCouponRecords(CouponRecordSearchDTO requestDto) {
		log.info("getCouponRecords requestDto: {}", requestDto);
		PageResponseDTO<CouponRecordResponseDTO> coupons = couponService.getCouponRecords(requestDto);
		return ResponseEntity.ok(coupons);
	}
	

}
