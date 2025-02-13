package com.himedia.luckydokiapi.domain.review.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReviewResponseDTO {
    //리뷰 보기
//    private Long id;
    private double rating;
    //    private String email;
    private String memberNickname;
    private Long shopId;
    private Long productId;
    private String productName;
    private String content;
    private String imageName;
    private LocalDateTime createdAt;
}
