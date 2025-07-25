package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.himedia.luckydokiapi.domain.coupon.entity.QCoupon.coupon;
import static com.himedia.luckydokiapi.domain.coupon.entity.QCouponRecord.couponRecord;
import static com.himedia.luckydokiapi.domain.member.entity.QMember.member;


@Repository
@RequiredArgsConstructor
public class CouponRecordRepositoryImpl implements CouponRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CouponRecord> findListBy(CouponRecordSearchDTO requestDto) {

        Pageable pageable = PageRequest.of(
                requestDto.getPage() - 1,
                requestDto.getSize(),
                "asc".equals(requestDto.getSort()) ?
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        List<CouponRecord> list = queryFactory
                .selectFrom(couponRecord)
                .leftJoin(couponRecord.coupon, coupon)
                .leftJoin(couponRecord.member, member)
                .where(
                        containsCouponRecordSearchKeyword(requestDto.getSearchKeyword())
                )
                .orderBy(couponRecord.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(couponRecord.count())
                .from(couponRecord)
                .leftJoin(couponRecord.coupon, coupon)
                .leftJoin(couponRecord.member, member)
                .where(
                        containsCouponRecordSearchKeyword(requestDto.getSearchKeyword())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(list, pageable, () -> total != null ? total : 0L);
    }

    @Override
    public List<Coupon> findCouponListByMemberEmail(String email) {

        return queryFactory
                .select(couponRecord.coupon)
                .from(couponRecord)
                .join(couponRecord.member, member).on(member.email.eq(email))
                .orderBy(couponRecord.id.desc())
                .fetch();
    }

    private BooleanExpression eqEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return member.email.eq(email);
    }

    private BooleanExpression containsSearchKeyword(String searchKeyword) {

        if (searchKeyword == null || searchKeyword.isBlank()) {
            return null;
        }

        return coupon.code.contains(searchKeyword)
                .or(coupon.name.contains(searchKeyword));
    }

    private BooleanExpression containsCouponRecordSearchKeyword(String searchKeyword) {

        if (searchKeyword == null || searchKeyword.isBlank()) {
            return null;
        }

        return couponRecord.coupon.code.contains(searchKeyword)
                .or(couponRecord.coupon.name.contains(searchKeyword))
                .or(couponRecord.member.email.contains(searchKeyword));
    }
}
