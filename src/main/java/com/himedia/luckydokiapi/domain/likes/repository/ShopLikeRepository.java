package com.himedia.luckydokiapi.domain.likes.repository;

import com.himedia.luckydokiapi.domain.likes.entity.ProductLike;
import com.himedia.luckydokiapi.domain.likes.entity.ShopLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShopLikeRepository extends JpaRepository<ShopLike, Long> {

    Boolean existsByMember_EmailAndShop_Id(final String memberEmail, final Long shopId);

    @Transactional
    @Modifying
    @Query("delete from ShopLike pl where pl.shop.id =:id and pl.member.email =:email")
    void deleteByEmailAndShopId(@Param("email") String email, @Param("id") Long id);


    @Query("select m from ShopLike m where m.member.email =:email")
    List<ShopLike> findByEmail(@Param("email") String email);


    @Query("SELECT COUNT(s) > 0 FROM ShopLike s WHERE s.member.email = :email AND s.shop.id = :shopId")
    boolean likes(@Param("email") String email, @Param("shopId") Long shopId);

    @Modifying
    @Query("DELETE FROM ShopLike s WHERE s.member.email = :email")
    void deleteByEmail(@Param("email") String email);


}
