package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CouponRepositoryCustom {
	List<Coupon> findActiveCoupons();
	Coupon findByCode(String code);
	Optional<Coupon> findCouponById(Long id); // 🔥 메서드 이름 변경
	void updateCoupon(Long id, CouponRequestDto couponRequestDto);
    Page<Coupon> findListBy(CouponRequestDto requestDto);
}