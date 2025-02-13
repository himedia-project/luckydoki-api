package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;

public interface ShopService {
    ShopResponseDTO getShopProfileById(Long shopId);
}
