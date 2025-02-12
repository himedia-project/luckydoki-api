package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.QCoupon;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;
	
	@Override
	public List<Coupon> findActiveCoupons() {
		QCoupon coupon = QCoupon.coupon;
		return queryFactory.selectFrom(coupon)
				.where(coupon.status.eq(CouponStatus.ACTIVE))
				.fetch();
	}
	
	@Override
	public Optional<Coupon> findCouponById(Long id) {
		QCoupon coupon = QCoupon.coupon;
		Coupon result = queryFactory.selectFrom(coupon)
				.where(coupon.id.eq(id))
				.fetchFirst();
		
		return Optional.ofNullable(result);
	}
	
	@Override
	public Coupon findByCode(String code) {
		QCoupon coupon = QCoupon.coupon;
		return queryFactory.selectFrom(coupon)
				.where(coupon.code.eq(code))
				.fetchFirst();
	}
	
	@Override
	@Transactional
	public void updateCoupon(Long id, CouponRequestDto couponRequestDto) {
		QCoupon coupon = QCoupon.coupon;
		
		queryFactory.update(coupon)
				.where(coupon.id.eq(id))
				.set(coupon.code, couponRequestDto.getCode())
				.set(coupon.name, couponRequestDto.getName())
				.set(coupon.content, couponRequestDto.getContent())
				.set(coupon.status, couponRequestDto.getStatus())
				.execute();
		
		entityManager.flush();
	}
}
