package com.himedia.luckydokiapi.domain.coupon.service;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.repository.CouponRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
	private final CouponRepository couponRepository;
	private final JPAQueryFactory queryFactory;
	
	private CouponDto convertToDto(Coupon coupon) {
		return new CouponDto(
				coupon.getId(),
				coupon.getCode(),
				coupon.getName(),
				coupon.getContent(),
				coupon.getStartDate(),
				coupon.getEndDate(),
				coupon.getStatus()
		);
	}
	
	@Override
	public List<CouponDto> getAllCoupons() {
		List<Coupon> coupons = couponRepository.findAll();
		return coupons.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CouponDto> getActiveCoupons() {
		List<Coupon> activeCoupons = couponRepository.findActiveCoupons();
		return activeCoupons.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public CouponDto getCouponById(Long id) {
		Coupon coupon = couponRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
		return convertToDto(coupon);
	}
	
	@Override
	public CouponDto getCouponByCode(String code) {
		Coupon coupon = couponRepository.findByCode(code);
		if (coupon == null) {
			throw new EntityNotFoundException("Coupon not found with code: " + code);
		}
		return convertToDto(coupon);
	}
	
	@Override
	@Transactional
	public CouponDto createCoupon(CouponDto couponDto) {
		Coupon coupon = new Coupon(
				couponDto.getId(),
				couponDto.getCode(),
				couponDto.getName(),
				couponDto.getContent(),
				couponDto.getStartDate(),
				couponDto.getEndDate(),
				couponDto.getStatus()
		);
		Coupon savedCoupon = couponRepository.save(coupon);
		return convertToDto(savedCoupon);
	}
	
	@Override
	@Transactional
	public CouponDto updateCoupon(Long id, CouponDto couponDto) {
		couponRepository.updateCoupon(id, couponDto);
		return getCouponById(id);
	}
	
	@Override
	@Transactional
	public void deleteCoupon(Long id) {
		Coupon coupon = couponRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
		couponRepository.delete(coupon);
	}
}