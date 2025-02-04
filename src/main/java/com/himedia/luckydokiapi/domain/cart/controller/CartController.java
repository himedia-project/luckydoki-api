package com.himedia.luckydokiapi.domain.cart.controller; // 올바른 패키지 경로


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListDTO;
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
        log.info("--------------------------------------------");
        log.info("email: " + email);
        return cartService.getCartItemList(email); // 장바구니 아이템 목록 조회
    }

    // 장바구니에 상품 추가
    @PostMapping("/add")
    public List<CartItemListDTO> addCartItem(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Valid @RequestBody CartItemDTO itemDTO
    ) {
        log.info("addCartItem.......... memberDTO: {}, itemDTO: {}", memberDTO, itemDTO);
        itemDTO.setEmail(memberDTO.getEmail()); // 이메일 설정
        return cartService.addCartItem(itemDTO); // 아이템 추가
    }

    // 장바구니 상품 삭제
    @DeleteMapping("/{id}")
    public List<CartItemListDTO> removeFromCart(
            @PathVariable("id") Long id
    ) {
        log.info("Removing cart item no: " + id);
        return cartService.removeCartItem(id); // 아이템 삭제
    }
}
