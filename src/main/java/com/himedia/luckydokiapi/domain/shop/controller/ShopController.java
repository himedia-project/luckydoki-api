package com.himedia.luckydokiapi.domain.shop.controller;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ProductService productService;

    // 특정 샵(셀러) 조회 API
    @GetMapping("/{shopId}")
    public ResponseEntity<SellerResponseDTO> findById(@PathVariable Long shopId) {
        log.info("shopId: {}", shopId);
        return ResponseEntity.ok(shopService.getSellerProfileById(shopId));
    }
}

