package com.himedia.luckydokiapi.domain.coupon.controller;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponIssueRequestDTO;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDTO;
import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.security.MemberDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
@Tag(name = "coupon-api", description ="쿠폰 코드로 사용가능한 쿠폰을 조회 하는 api ")
public class CouponController {
	private final CouponService couponService;

	// 쿠폰 코드로 조회
	@GetMapping("/{code}")
	public CouponResponseDTO getCouponByCode(@Parameter(description = "쿠폰 코드 번호")@PathVariable String code) {
		
		log.info("getCouponByCode: {}", code);
		return couponService.getCouponByCode(code);
	}

	// 해당 유저가 쿠폰 코드 입력후 쿠폰발급
	@PostMapping("/issue")
	public ResponseEntity<?> issueCouponToUser(@RequestBody CouponIssueRequestDTO requestDTO,
											   @AuthenticationPrincipal MemberDTO memberDTO
	) {
		String email = memberDTO == null ? null : memberDTO.getEmail();
		log.info("issueCouponToUser code: {}, email: {}", requestDTO.getCode(), email);
		Long couponId = couponService.issueCouponToUser(requestDTO.getCode(), email);
		return ResponseEntity.ok(couponId);
	}
}
