package com.himedia.luckydokiapi.domain.coupon.repository;

import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.himedia.luckydokiapi.domain.coupon.repository.querydsl.CouponRecordRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRecordRepository extends JpaRepository<CouponRecord, Long>
, CouponRecordRepositoryCustom {

    @Query("SELECT COUNT(cr) > 0 FROM CouponRecord cr WHERE cr.coupon.id = :couponId AND cr.member.email = :email")
    boolean existsByCouponIdAndMemberEmail(@Param("couponId") Long couponId, @Param("email") String email);

    @Query("SELECT COUNT(cr) > 0 FROM CouponRecord cr WHERE cr.coupon.id = :couponId")
    boolean existsByCouponId(@Param("couponId") Long couponId);

    @Modifying
    @Query("DELETE FROM CouponRecord cr WHERE cr.member.email = :email")
    void deleteByMemberEmail(@Param("email") String email);

    @Query("SELECT cr FROM CouponRecord cr WHERE cr.member.email = :email AND cr.coupon = :coupon")
    Optional<CouponRecord> findByMemberAndCoupon(@Param("email") String email, @Param("coupon") Coupon coupon);
}
