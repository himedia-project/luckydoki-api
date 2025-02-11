package com.himedia.luckydokiapi.domain.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReviewRequestDTO {
    //리뷰 등록
    private double rating;
    private String email;
//    private Long shopId;
    private Long productId;
    private String content;
    private MultipartFile image;

}
