package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;

public interface ShopService {
    SellerResponseDTO getSellerProfileById(Long shopId);
}
