package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopProductResponseDTO;

public interface ShopService {
    ShopResponseDTO getShopProfileById(Long shopId);
    ShopProductResponseDTO getShopProducts(Long shopId, String email);
}
