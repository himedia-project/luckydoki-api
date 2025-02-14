package com.himedia.luckydokiapi.domain.shop.controller;

import com.himedia.luckydokiapi.domain.shop.dto.ShopProductResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.service.ShopService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 특정 샵(셀러) 정보 조회 API
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopResponseDTO> findById(
            @PathVariable Long shopId,
            @AuthenticationPrincipal MemberDTO memberDTO) {

        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("shopId: {}, email: {}", shopId, email);

        return ResponseEntity.ok(shopService.getShopProfileById(shopId, email));
    }

    @GetMapping("/{shopId}/product/list")
    public ResponseEntity<ShopProductResponseDTO> findShopProducts(
            @PathVariable Long shopId,
            @AuthenticationPrincipal MemberDTO memberDTO) { // ✅ 현재 로그인한 사용자 정보 가져오기

        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("shopId: {}, email: {}", shopId, email);

        return ResponseEntity.ok(shopService.getShopProducts(shopId, email));
    }
}

