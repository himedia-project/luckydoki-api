package com.himedia.luckydokiapi.domain.shop.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponseDTO {

    private Long id;
    private String image;         // 샵 이미지 (S3 URL)
    private String introduction;  // 샵 소개글
    private String email;         // 샵 주인의 이메일
    private String nickName;      // 샵 주인의 닉네임
    private Boolean likes;



}
