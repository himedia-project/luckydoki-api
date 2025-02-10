package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.QCoupon;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom{
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<Coupon> findActiveCoupons() {
		QCoupon coupon = QCoupon.coupon;
		return queryFactory.selectFrom(coupon)
				.where(coupon.status.eq(CouponStatus.ACTIVE))
				.fetch();
	}
	
	@Override
	@Transactional
	public void updateCoupon(Long id, CouponDto couponDto) {
		QCoupon coupon = QCoupon.coupon;
		queryFactory.update(coupon)
				.where(coupon.id.eq(id))
				.set(coupon.code, couponDto.getCode())
				.set(coupon.name, couponDto.getName())
				.set(coupon.content, couponDto.getContent())
				.set(coupon.startDate, couponDto.getStartDate())
				.set(coupon.endDate, couponDto.getEndDate())
				.set(coupon.status, couponDto.getStatus())
				.execute();
	}
}
