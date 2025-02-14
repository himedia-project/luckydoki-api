package com.himedia.luckydokiapi.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityImage;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponseDTO {
    private Long id;
    private String nickName;
    private String title;
    private String content;
    private List<String> uploadFileNames;
    private List<Long> productIds;
    private List<ProductDTO.Response> productDTOs;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;


    public static CommunityResponseDTO from(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .title(community.getTitle())
                .content(community.getContent())
                .nickName(community.getMember().getNickName())
                .uploadFileNames(community.getImageList().stream().map(CommunityImage::getImageName).toList())
                .productIds(community.getCommunityProductList().stream().map(communityProduct -> communityProduct.getProduct().getId()).toList())
                .productDTOs(community.getCommunityProductList().stream().map(communityProduct -> ProductDTO.Response.from(communityProduct.getProduct())).toList())
                .createdAt(community.getCreatedAt())
                .build();
    }

}
