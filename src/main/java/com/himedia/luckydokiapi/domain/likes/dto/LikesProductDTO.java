package com.himedia.luckydokiapi.domain.likes.dto;

import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductEvent;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Schema(description = "좋아요한 상품 정보 DTO")
public class LikesProductDTO {

    private Long id;
    private Long productId;
    private String email;
    private Integer price;
    private Integer discountPrice;
    private Integer discountRate;
    private String productName;
    private String productCode;
    private Integer likesCount;
    private String productImageUrl;
    private ProductIsNew isNew;
    private ProductBest best;
    private ProductEvent event;
    private Boolean likes;
    private double reviewAverage;
    private int reviewCount;

}
