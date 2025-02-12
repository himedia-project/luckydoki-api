package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.dto.CouponResponseDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.NumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
	public PageResponseDTO<CouponResponseDto> getAllCoupons(CouponRequestDto requestDto) {
		Page<Coupon> result = couponRepository.findListBy(requestDto);

		return PageResponseDTO.<CouponResponseDto>withAll()
				.dtoList(result.getContent().stream().map(this::convertToResponseDto).toList())
				.totalCount(result.getTotalElements())
				.pageRequestDTO(requestDto)
				.build();
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
	public Long createCoupon(CouponRequestDto requestDto) {
		Coupon coupon = Coupon.builder()
				.code(NumberGenerator.generateRandomNumber(10)) // ✅ 랜덤 코드 생성
				.name(requestDto.getName())
				.content(requestDto.getContent())
				.status(CouponStatus.ACTIVE) // ✅ 활성화 상태로 자동 설정
				.startDate(requestDto.getStartDate()) // ✅ 현재 날짜 자동 설정
				.endDate(requestDto.getEndDate()) // ✅ 3개월 후 자동 설정
				.build();
		
		Coupon savedCoupon = couponRepository.save(coupon);
		return savedCoupon.getId();
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
