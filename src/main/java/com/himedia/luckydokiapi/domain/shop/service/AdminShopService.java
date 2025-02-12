package com.himedia.luckydokiapi.domain.shop.service;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.member.entity.SellerApplication;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;

import java.util.List;

public interface AdminShopService {

    Long approveSeller(Long applicationId);

    List<SellerResponseDTO> getPendingApplications();

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
                .build();
    }

}
