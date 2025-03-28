package com.himedia.luckydokiapi.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityImage;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "커뮤니티 글 응답용 dto")
public class CommunityResponseDTO {
    private Long id;
    private Long shopId;
    private String authorImage;
    private String nickName;
    private String title;
    private String content;
    private List<String> uploadFileNames;
    private List<ProductDTO.Response> productDTOs;
    private List<TagDTO> tagList;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;


    public static CommunityResponseDTO toDto(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .shopId(community.getMember().getShop() == null ? null : community.getMember().getShop().getId())
                .nickName(community.getMember().getNickName())
                .title(community.getTitle())
                .authorImage(community.getMember().getShop() == null ? community.getMember().getProfileImage() : community.getMember().getShop().getImage())
                .content(community.getContent())
                .uploadFileNames(community.getImageList().stream().map(CommunityImage::getImageName).toList())
                .productDTOs(community.getCommunityProductList().stream().map(communityProduct -> ProductDTO.Response.toDto(communityProduct.getProduct())).toList())
                .createdAt(community.getCreatedAt())
                .tagList(community.getTagList())
                .build();
    }

}
