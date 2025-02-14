package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopProductResponseDTO;

public interface ShopService {
    ShopResponseDTO getShopProfileById(Long shopId, String email);
    ShopProductResponseDTO getShopProducts(Long shopId, String email);
}
