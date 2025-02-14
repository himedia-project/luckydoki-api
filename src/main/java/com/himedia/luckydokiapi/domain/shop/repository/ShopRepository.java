package com.himedia.luckydokiapi.domain.shop.repository;

import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.querydsl.ShopRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>
, ShopRepositoryCustom {
    @Query("select m from Shop m where m.member.email = :email")
    Optional<Shop> findByMemberEmail(@Param("email") String email);

    @Query("SELECT s FROM Shop s LEFT JOIN FETCH s.productList WHERE s.id = :shopId")
    Optional<Shop> findByIdWithProducts(@Param("shopId") Long shopId);

    @Query("SELECT s FROM Shop s LEFT JOIN s.member.communityList WHERE s.id = :shopId")
    Optional<Shop> findByIdWithCommunities(@Param("shopId") Long shopId);
}
