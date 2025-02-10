package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;

import java.util.List;

public interface CouponRepositoryCustom {
	List<Coupon> findActiveCoupons();
	Coupon findByCode(String code);
	void updateCoupon(Long id, CouponDto couponDto);
}