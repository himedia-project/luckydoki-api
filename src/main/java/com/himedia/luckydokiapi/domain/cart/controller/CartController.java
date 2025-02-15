package com.himedia.luckydokiapi.domain.cart.controller; // 올바른 패키지 경로


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListIdDTO;
import com.himedia.luckydokiapi.domain.cart.service.CartService;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService; // CartService 주입

    // 장바구니 목록 조회
    @GetMapping("/item/list")
    public List<CartItemListDTO> getCartItems(@AuthenticationPrincipal MemberDTO memberDTO) {
        String email = memberDTO.getEmail();
        log.info("email: {}", email);
        return cartService.getCartItemList(email); // 장바구니 아이템 목록 조회
    }

    // 장바구니에 상품 추가
    @PostMapping
    public List<CartItemListDTO> addCartItem(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Valid @RequestBody CartItemDTO itemDTO
    ) {
        log.info("addCartItem.......... memberDTO: {}, itemDTO: {}", memberDTO, itemDTO);
        return cartService.addCartItem(memberDTO.getEmail(), itemDTO); // 아이템 추가
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/{cartItemId}")
    public List<CartItemListDTO> removeFromCart(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal MemberDTO memberDTO
    ) {
        log.info("removeFromCart memberDTO: {} , cart item id: {}", memberDTO, cartItemId);
        return cartService.removeCartItem(memberDTO.getEmail(), cartItemId); // 아이템 삭제
    }

    // 장바구니 상품 전체삭제
    @DeleteMapping("/all")
    public List<CartItemListDTO> removeFromCartAll(
            @RequestBody CartItemListIdDTO requestDTO,
            @AuthenticationPrincipal MemberDTO memberDTO
    ) {
        log.info("removeFromCartAll memberDTO: {} , cartitem requestDTO: {}", memberDTO, requestDTO);
        return cartService.removeCartItemAll(memberDTO.getEmail(), requestDTO.getCartItemIdList());
    }
}
