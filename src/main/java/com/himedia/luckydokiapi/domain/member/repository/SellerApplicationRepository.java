package com.himedia.luckydokiapi.domain.member.repository;

import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {

    @Query("select sa from SellerApplication sa where sa.approved = :approved")
    List<SellerApplication> findByIsApproved(@Param("approved") ShopApproved approved);

    Optional<SellerApplication> findByEmail(String email);
}
