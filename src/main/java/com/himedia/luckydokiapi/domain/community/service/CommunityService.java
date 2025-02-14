package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityImage;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopCommunityResponseDTO;

import java.util.ArrayList;
import java.util.List;

public interface CommunityService {
    CommunityResponseDTO getCommunityById(Long communityId);
    List<CommunityResponseDTO> getAllCommunities(CommunitySearchDTO request);
    List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email);
    CommunityResponseDTO postCommunity(String email, CommunityRequestDTO request);
//    CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request);
    void deleteCommunity(Long communityId, String email);
    ShopCommunityResponseDTO getShopCommunities(Long shopId, String email);



    default CommunityResponseDTO toDTO(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .nickName(community.getMember().getNickName())
                .title(community.getTitle())
                .content(community.getContent())
                .uploadFileNames(
                        community.getImageList().stream()
                                .map(CommunityImage::getImageName)
                                .toList()
                )
                .productIds(
                        community.getCommunityProductList().stream()
                                .map(communityProduct -> communityProduct.getProduct().getId())
                                .toList()
                )
                .createdAt(community.getCreatedAt())
                .build();
    }
}
