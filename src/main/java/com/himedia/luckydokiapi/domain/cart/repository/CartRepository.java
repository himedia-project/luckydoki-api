package com.himedia.luckydokiapi.domain.cart.repository;

import com.himedia.luckydokiapi.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 사용자의 이메일로 장바구니를 조회하는 메소드
    @Query("select cart from Cart cart where cart.member.email = :email")
    Optional<Cart> getCartOfMember(@Param("email") String email);
}
