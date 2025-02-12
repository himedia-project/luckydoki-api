package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRepository;
import com.himedia.luckydokiapi.domain.coupon.repository.querydsl.CouponRepositoryCustom;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
	private final CouponRepository couponRepository;
	
	@Override
	public List<CouponResponseDto> getAllCoupons() {
		return couponRepository.findAll().stream()
				.map(this::convertToResponseDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CouponResponseDto> getActiveCoupons() {
		return couponRepository.findActiveCoupons().stream()
				.map(this::convertToResponseDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public CouponResponseDto getCouponById(Long id) {
		Coupon coupon = couponRepository.findCouponById(id)
				.orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
		return convertToResponseDto(coupon);
	}
	
	@Override
	public CouponResponseDto getCouponByCode(String code) {
		Coupon coupon = couponRepository.findByCode(code);
		if (coupon == null) {
			throw new EntityNotFoundException("Coupon not found with code: " + code);
		}
		return convertToResponseDto(coupon);
	}
	
	@Override
	@Transactional
	public CouponResponseDto createCoupon(CouponRequestDto requestDto) {
		Coupon coupon = Coupon.builder()
				.code(requestDto.getCode())
				.name(requestDto.getName())
				.content(requestDto.getContent())
				.status(requestDto.getStatus())
				.startDate(LocalDate.now()) // ✅ 현재 날짜 자동 설정
				.endDate(LocalDate.now().plusMonths(3)) // ✅ 3개월 후 자동 설정
				.build();
		
		Coupon savedCoupon = couponRepository.save(coupon);
		return convertToResponseDto(savedCoupon);
	}
	
	@Override
	@Transactional
	public CouponResponseDto updateCoupon(Long id, CouponRequestDto requestDto) {
		// ✅ QueryDSL 기반의 updateCoupon() 호출
		couponRepository.updateCoupon(id, requestDto);
		return getCouponById(id);
	}
	
	@Override
	@Transactional
	public void deleteCoupon(Long id) {
		Coupon coupon = couponRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
		couponRepository.delete(coupon);
	}
	
	private CouponResponseDto convertToResponseDto(Coupon coupon) {
		return CouponResponseDto.builder()
				.id(coupon.getId())
				.code(coupon.getCode())
				.name(coupon.getName())
				.content(coupon.getContent())
				.startDate(coupon.getStartDate())
				.endDate(coupon.getEndDate())
				.status(coupon.getStatus())
				.build();
	}
}
