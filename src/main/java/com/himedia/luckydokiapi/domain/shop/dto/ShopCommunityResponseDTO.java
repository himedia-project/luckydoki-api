package com.himedia.luckydokiapi.domain.shop.dto;

import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCommunityResponseDTO {
    private Long shopId;
    private String shopName; // 샵 주인의 닉네임
    private List<CommunityResponseDTO> communityList;

    public static ShopCommunityResponseDTO from(Shop shop) {

        return ShopCommunityResponseDTO.builder()
                .shopId(shop.getId())
                .shopName(shop.getMember().getNickName())
                .communityList(shop.getMember().getCommunityList().stream()
                        .map(CommunityResponseDTO::from).toList())
                .build();
    }
}
