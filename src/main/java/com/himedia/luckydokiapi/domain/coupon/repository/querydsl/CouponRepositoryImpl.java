package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRequestDto;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.QCoupon;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.himedia.luckydokiapi.domain.coupon.entity.QCoupon.coupon;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;


	@Override
	public Page<Coupon> findListBy(CouponRequestDto requestDto) {

		Pageable pageable = PageRequest.of(
				requestDto.getPage() - 1,
				requestDto.getSize(),
				"asc".equals(requestDto.getSort()) ?
						Sort.by("id").ascending() : Sort.by("id").descending()
		);

		List<Coupon> list = queryFactory
				.selectFrom(coupon)
				.where(
						containsSearchKeyword(requestDto.getSearchKeyword())
				)
				.orderBy(coupon.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Coupon> countQuery = queryFactory
				.selectFrom(coupon)
				.where(
						containsSearchKeyword(requestDto.getSearchKeyword())
				);

		return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
	}

	private BooleanExpression containsSearchKeyword(String searchKeyword) {
		if (searchKeyword == null) {
			return null;
		}
		return coupon.code.contains(searchKeyword)
				.or(coupon.name.contains(searchKeyword));
	}

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
	@Transactional
	public void updateCoupon(Long id, CouponRequestDto couponRequestDto) {
		QCoupon coupon = QCoupon.coupon;
		
		queryFactory.update(coupon)
				.where(coupon.id.eq(id))
				.set(coupon.name, couponRequestDto.getName())
				.set(coupon.content, couponRequestDto.getContent())
				.set(coupon.status, couponRequestDto.getStatus())
				.execute();
		
		entityManager.flush();
	}
}
