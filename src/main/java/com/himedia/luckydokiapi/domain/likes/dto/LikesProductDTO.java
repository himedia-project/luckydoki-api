package com.himedia.luckydokiapi.domain.likes.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class LikesProductDTO {

    private Long id;
    private Long productId;
    private String email;
    private String productName;
    private String productCode;
    private Integer likesCount;
    private String productImageUrl;
}
