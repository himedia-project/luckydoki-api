package com.himedia.luckydokiapi.domain.coupon.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponIssueRequestDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
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
	public ResponseEntity<PageResponseDTO<CouponResponseDTO>> getAllCoupons(CouponRequestDto requestDto) {
		log.info("getAllCoupons requestDto: {}", requestDto);
		PageResponseDTO<CouponResponseDTO> coupons = couponService.getAllCoupons(requestDto);
		return ResponseEntity.ok(coupons);
	}
	
	// 활성화된 쿠폰 조회
	@GetMapping("/active")
	public ResponseEntity<List<CouponResponseDTO>> getActiveCoupons() {
		List<CouponResponseDTO> activeCoupons = couponService.getActiveCoupons();
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
	public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long id) {
		log.info("getCouponById: {}", id);
		CouponResponseDTO coupon = couponService.getCouponById(id);
		return ResponseEntity.ok(coupon);
	}
	
	// 쿠폰 정보 수정
	@PutMapping("/{id}")
	public ResponseEntity<CouponResponseDTO> updateCoupon(@PathVariable Long id, @RequestBody CouponRequestDto couponRequestDto) {
		log.info("updateCoupon: {}, {}", id, couponRequestDto);
		CouponResponseDTO updatedCoupon = couponService.updateCoupon(id, couponRequestDto);
		return ResponseEntity.ok(updatedCoupon);
	}
	
	// 쿠폰 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
		log.info("deleteCoupon: {}", id);
		couponService.deleteCoupon(id);
		return ResponseEntity.noContent().build();
	}

	// 쿠폰 발급
	@PostMapping("/{couponId}/issue")
	public ResponseEntity<Void> issueCoupon(@PathVariable Long couponId, @RequestBody CouponIssueRequestDTO requestDTO) {
		log.info("issueCoupon couponId: {}", couponId);
		couponService.issueCoupon(couponId, requestDTO.getEmails());
		return ResponseEntity.ok().build();
	}
}
