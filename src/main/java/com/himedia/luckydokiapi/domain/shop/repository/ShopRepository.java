package com.himedia.luckydokiapi.domain.shop.repository;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select m from Shop m where m.member.email = :email")
    Optional<Shop> findByMemberEmail(@Param("email") String email);


}
