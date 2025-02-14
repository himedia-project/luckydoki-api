package com.himedia.luckydokiapi.domain.community.dto;

import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
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
    private LocalDateTime createdAt;

    public CommunityResponseDTO(Community community) {
        this.id = community.getId();
        this.nickName = community.getMember().getNickName();
        this.title = community.getTitle();
        this.content = community.getContent();
        this.createdAt = community.getCreatedAt();
        this.uploadFileNames = (community.getImageList() != null) ?
                community.getImageList().stream()
                        .map(CommunityImage::getImageName)
                        .toList()
                : new ArrayList<>();


        this.productIds =
                community.getCommunityProductList().stream()
                        .map(communityProduct -> communityProduct.getProduct().getId())
                        .toList();


    }


}
