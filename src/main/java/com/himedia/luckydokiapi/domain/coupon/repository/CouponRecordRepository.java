package com.himedia.luckydokiapi.domain.coupon.repository;

import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.himedia.luckydokiapi.domain.coupon.repository.querydsl.CouponRecordRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRecordRepository extends JpaRepository<CouponRecord, Long>
, CouponRecordRepositoryCustom {

    @Query("SELECT COUNT(cr) > 0 FROM CouponRecord cr WHERE cr.coupon.id = :couponId AND cr.member.email = :email")
    boolean existsByCouponIdAndMemberEmail(@Param("couponId") Long couponId, @Param("email") String email);

    @Query("SELECT COUNT(cr) > 0 FROM CouponRecord cr WHERE cr.coupon.id = :couponId")
    boolean existsByCouponId(@Param("couponId") Long couponId);
}
