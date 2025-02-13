package com.himedia.luckydokiapi.domain.shop.controller;

import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 특정 샵(셀러) 조회 API (shopId 기준)
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopResponseDTO> findById(@PathVariable Long shopId) {
        log.info("shopId: {}", shopId);
        return ResponseEntity.ok(shopService.getShopProfileById(shopId));
    }
}
