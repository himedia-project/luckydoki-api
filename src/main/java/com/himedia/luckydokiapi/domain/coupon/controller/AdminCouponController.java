package com.himedia.luckydokiapi.domain.coupon.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {
	private final CouponService couponService;

	// 모든 쿠폰 목록 조회
	@GetMapping("/list")
	public ResponseEntity<PageResponseDTO<CouponResponseDto>> getAllCoupons(CouponRequestDto requestDto) {
		log.info("getAllCoupons requestDto: {}", requestDto);
		PageResponseDTO<CouponResponseDto> coupons = couponService.getAllCoupons(requestDto);
		return ResponseEntity.ok(coupons);
	}
	
	// 활성화된 쿠폰 조회
	@GetMapping("/active")
	public ResponseEntity<List<CouponResponseDto>> getActiveCoupons() {
		List<CouponResponseDto> activeCoupons = couponService.getActiveCoupons();
		return ResponseEntity.ok(activeCoupons);
	}
	
	// 새로운 쿠폰 생성
	@PostMapping
	public ResponseEntity<Long> createCoupon(@Valid @RequestBody CouponRequestDto couponRequestDto) {
		log.info("createCoupon couponRequestDto: {}", couponRequestDto);
		Long couponId = couponService.createCoupon(couponRequestDto);
		return ResponseEntity.ok(couponId);
	}
	
	// 쿠폰 아이디로 조회
	@GetMapping("/{id}")
	public ResponseEntity<CouponResponseDto> getCouponById(@PathVariable Long id) {
		log.info("getCouponById: {}", id);
		CouponResponseDto coupon = couponService.getCouponById(id);
		return ResponseEntity.ok(coupon);
	}
	
	// 쿠폰 정보 수정
	@PutMapping("/{id}")
	public ResponseEntity<CouponResponseDto> updateCoupon(@PathVariable Long id, @RequestBody CouponRequestDto couponRequestDto) {
		log.info("updateCoupon: {}, {}", id, couponRequestDto);
		CouponResponseDto updatedCoupon = couponService.updateCoupon(id, couponRequestDto);
		return ResponseEntity.ok(updatedCoupon);
	}
	
	// 쿠폰 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
		log.info("deleteCoupon: {}", id);
		couponService.deleteCoupon(id);
		return ResponseEntity.noContent().build();
	}
}
