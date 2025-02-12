package com.himedia.luckydokiapi.domain.shop.repository.querydsl;

import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.shop.dto.SellerSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerApplicationRepositoryCustom {

    Page<SellerApplication> findListBy(SellerSearchDTO request, Pageable pageable);
}
