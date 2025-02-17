package com.himedia.luckydokiapi.domain.cart.controller; // 올바른 패키지 경로


import com.himedia.luckydokiapi.domain.cart.dto.CartItemChangeDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListIdDTO;
import com.himedia.luckydokiapi.domain.cart.service.CartService;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SELLER')")
    @GetMapping("/item/list")
    public List<CartItemListDTO> getCartItems(@AuthenticationPrincipal MemberDTO memberDTO) {
        String email = memberDTO.getEmail();
        log.info("email: {}", email);
        return cartService.getCartItemList(email); // 장바구니 아이템 목록 조회
    }

    // 장바구니에 상품 추가
    @PreAuthorize("#memberDTO.getEmail() == authentication.name")
    @PostMapping("/item")
    public List<CartItemListDTO> addCartItem(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Valid @RequestBody CartItemDTO itemDTO
    ) {
        log.info("addCartItem.......... memberDTO: {}, itemDTO: {}", memberDTO, itemDTO);
        return cartService.addCartItem(memberDTO.getEmail(), itemDTO); // 아이템 추가
    }

    // 장바구니 수량 변경
    @PreAuthorize("#memberDTO.getEmail() == authentication.name")
    @PostMapping("/item/{cartItemId}/qty")
    public List<CartItemListDTO> changeCartItemQty(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Valid @RequestBody CartItemChangeDTO itemDTO
    ) {
        log.info("changeCartItemQty.......... cartItemId: {}, memberDTO: {}, itemDTO gty: {}",cartItemId, memberDTO, itemDTO);
        // 삭제 처리
        if (itemDTO.getQty() <= 0) {
            return cartService.removeCartItem(memberDTO.getEmail(), cartItemId);
        }
        return cartService.changeCartItemQty(memberDTO.getEmail(), cartItemId, itemDTO.getQty());
    }


    // 장바구니 상품 삭제
    @PreAuthorize("#memberDTO.getEmail() == authentication.name")
    @DeleteMapping("/item/{cartItemId}")
    public List<CartItemListDTO> removeFromCart(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal MemberDTO memberDTO
    ) {
        log.info("removeFromCart memberDTO: {} , cart item id: {}", memberDTO, cartItemId);
        return cartService.removeCartItem(memberDTO.getEmail(), cartItemId); // 아이템 삭제
    }

    // 장바구니 상품 전체삭제
    @PreAuthorize("#memberDTO.getEmail() == authentication.name")
    @DeleteMapping("/item/list")
    public List<CartItemListDTO> removeFromCartItemList(
            @RequestBody CartItemListIdDTO requestDTO,
            @AuthenticationPrincipal MemberDTO memberDTO
    ) {
        log.info("removeFromCartItemList memberDTO: {} , cartitem requestDTO: {}", memberDTO, requestDTO);
        return cartService.removeFromCartItemList(memberDTO.getEmail(), requestDTO.getCartItemIdList());
    }
}
