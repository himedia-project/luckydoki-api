package com.himedia.luckydokiapi.domain.likes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Schema(description = "좋아요한 shop 정보 DTO")
public class LikesShopDTO {

    private Long id;
    private String email;
    private Long shopId;
    private String sellerEmail;
    private String sellerNickname;
    private String shopImageUrl;
    private Boolean likes;
}
