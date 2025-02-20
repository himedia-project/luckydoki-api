package com.himedia.luckydokiapi.domain.cart.repository;

import com.himedia.luckydokiapi.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 사용자의 이메일을 통해 장바구니 아이템을 조회하는 메소드
    @Query("select ci from CartItem ci where ci.cart.member.email = :email")
    List<CartItem> getItemsOfCartList(@Param("email") String email);

    // 특정 상품 ID와 사용자의 이메일로 장바구니 아이템을 조회하는 메소드
    @Query("select ci from CartItem ci where ci.cart.member.email = :email and ci.product.id = :productId")
    CartItem getItemOfPno(@Param("email") String email, @Param("productId") Long productId);

    // 특정 장바구니 아이템의 장바구니 ID를 조회하는 메소드
    @Query("select ci.cart.id from CartItem ci where ci.id = :cartItemId")
    Long getCartFromItem(@Param("cartItemId") Long cartItemId);

    // 특정 장바구니 ID로 장바구니 아이템을 조회하는 메소드
    @Query("select ci from CartItem ci where ci.cart.id = :cartId")
    List<CartItem> getItemsOfCartByCartId(@Param("cartId") Long cartId);

    // 특정 상품 ID로 장바구니 아이템을 조회하는 메소드
    @Query("select ci from CartItem ci where ci.product.id = :productId")
    CartItem findByProductId(@Param("productId") Long productId);

    // 장바구니 아이템 ID 리스트로 장바구니 아이템을 조회하는 메소드
    @Query("select ci from CartItem ci where ci.id in :cartItemIdList")
    List<CartItem> findByIds(@Param("cartItemIdList") List<Long> cartItemIdList);

    // 장바구니 아이템 ID 리스트로 장바구니 아이템을 벌크삭제하는 메소드
    @Modifying
    @Query("delete from CartItem ci where ci.id in :cartItemIdList")
    void deleteBulk(@Param("cartItemIdList") List<Long> cartItemIdList);

    // 특정 상품 ID로 장바구니 아이템이 존재하는지 확인하는 메소드
    @Query("select case when count(ci) > 0 then true else false end from CartItem ci where ci.product.id = :productId")
    boolean existByProductId(@Param("productId") Long productId);
}
