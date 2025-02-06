package com.himedia.luckydokiapi.domain.member.repository;

import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerApplicationRepository extends JpaRepository<SellerApplication, Long> {

    @Query("select sa from SellerApplication sa where sa.isApproved = :isApproved")
    List<SellerApplication> findByIsApproved(@Param("isApproved") boolean isApproved);

    Optional<SellerApplication> findByEmail(String email);
}
