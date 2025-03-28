package com.himedia.luckydokiapi.domain.member.service;

import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.domain.shop.repository.querydsl.SellerApplicationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long>
, SellerApplicationRepositoryCustom {

    @Query("select sa from SellerApplication sa where sa.approved = :approved")
    List<SellerApplication> findByIsApproved(@Param("approved") ShopApproved approved);

    @Query("select sa from SellerApplication sa where sa.member.email = :email")
    Optional<SellerApplication> findByEmail(@Param("email") String email);

    @Query("select case when count(sa) > 0 then true else false end from SellerApplication sa where sa.member.email = :email")
    Boolean existsByEmail(@Param("email") String email);
}
