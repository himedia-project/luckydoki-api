package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.service.SellerApplicationRepository;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final SellerApplicationRepository sellerApplicationRepository;

    @Override
    @Transactional(readOnly = true)
    public SellerResponseDTO getSellerProfileById(Long shopId) {
        SellerApplication seller = sellerApplicationRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 셀러가 존재하지 않습니다: " + shopId));

        return SellerResponseDTO.builder()
                .id(seller.getId())
                .email(seller.getEmail())
                .nickName(seller.getNickName())
                .approved(seller.getApproved())
                .statusDescription(seller.getApproved().name())
                .shopImage(seller.getShopImage()) // S3 저장된 프로필 이미지 URL
                .introduction(seller.getIntroduction()) // 소개글
                .requestAt(seller.getCreatedAt()) // 신청 날짜
                .build();
    }
}
