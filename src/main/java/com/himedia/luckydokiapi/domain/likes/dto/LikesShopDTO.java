package com.himedia.luckydokiapi.domain.likes.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LikesShopDTO {

    private Long id;
    private String email;
    private Long shopId;
    private String sellerEmail;
//    private Integer likesCount;
}
