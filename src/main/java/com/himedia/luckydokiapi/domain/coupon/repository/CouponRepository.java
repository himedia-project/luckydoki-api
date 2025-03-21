package com.himedia.luckydokiapi.domain.coupon.repository;

import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.coupon.repository.querydsl.CouponRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {

    Optional<Coupon> findByCode(String code);
}
