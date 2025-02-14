package com.himedia.luckydokiapi.domain.shop.controller;

import com.himedia.luckydokiapi.domain.shop.dto.ShopProductResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ShopResponseDTO> findById(@PathVariable Long shopId) {
        log.info("shopId: {}", shopId);
        return ResponseEntity.ok(shopService.getShopProfileById(shopId));
    }

    @GetMapping("/{shopId}/product/list")
    public ResponseEntity<ShopProductResponseDTO> findShopProducts(
            @PathVariable Long shopId,
            @RequestParam(value = "email", required = false) Optional<String> email) { // Optional 처리

        log.info("shopId: {}, email: {}", shopId, email.orElse("Guest"));
        return ResponseEntity.ok(shopService.getShopProducts(shopId, email.orElse(null))); // Optional을 null 처리
    }
}

