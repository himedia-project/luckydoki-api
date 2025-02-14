package com.himedia.luckydokiapi.domain.coupon.repository.querydsl;

import com.himedia.luckydokiapi.domain.coupon.dto.CouponRecordSearchDTO;
import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CouponRecordRepositoryCustom {

    Page<CouponRecord> findListBy(CouponRecordSearchDTO requestDto);

    List<Coupon> findCouponListByMemberEmail(String email);
}
