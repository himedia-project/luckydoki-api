package com.himedia.luckydokiapi.domain.community.repository;

import com.himedia.luckydokiapi.domain.community.entity.CommunityProduct;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityProductRepository extends JpaRepository<CommunityProduct, Long> {

    @Modifying
    @Query("delete from CommunityProduct cp where cp.product = :product")
    void deleteByProduct(@Param("product") Product product);

    @Modifying
    @Query("DELETE FROM CommunityProduct cp WHERE cp.community.id IN (SELECT com.id FROM Community com WHERE com.member.email = :email)")
    void deleteByMemberEmail(@Param("email") String email);

}
