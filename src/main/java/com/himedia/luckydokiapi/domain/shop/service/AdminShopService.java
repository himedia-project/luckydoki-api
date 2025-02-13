package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.domain.shop.dto.SellerSearchDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopSearchDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface AdminShopService {

    PageResponseDTO<ShopResponseDTO> list(ShopSearchDTO request);

    Long approveSeller(Long applicationId);

    PageResponseDTO<SellerResponseDTO> getPendingApplications(SellerSearchDTO request);

    List<SellerResponseDTO> getApprovedApplications();

    default SellerResponseDTO convertToDTO(SellerApplication application) {
        return SellerResponseDTO.builder()
                .id(application.getId())
                .email(application.getEmail())
                .nickName(application.getNickName())
                .shopImage(application.getShopImage())
                .introduction(application.getIntroduction())
                .approved(application.getApproved())
                .statusDescription(application.getApproved() == ShopApproved.Y ? "승인 완료" : "승인 대기")
                .requestAt(application.getCreatedAt())
                .build();
    }


    default ShopResponseDTO convertToDTO(Shop shop) {
        return ShopResponseDTO.builder()
                .id(shop.getId())
                .email(shop.getMember().getEmail())
                .nickName(shop.getMember().getNickName())
                .image(shop.getImage())
                .introduction(shop.getIntroduction())
                .build();
    }


}
