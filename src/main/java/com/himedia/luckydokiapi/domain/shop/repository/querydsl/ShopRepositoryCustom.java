package com.himedia.luckydokiapi.domain.shop.repository.querydsl;

import com.himedia.luckydokiapi.domain.shop.dto.ShopSearchDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import org.springframework.data.domain.Page;

public interface ShopRepositoryCustom {

    Page<Shop> findListBy(ShopSearchDTO request);
}
