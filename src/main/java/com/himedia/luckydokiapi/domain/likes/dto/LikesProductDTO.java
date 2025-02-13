package com.himedia.luckydokiapi.domain.likes.dto;

import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductEvent;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
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
}
