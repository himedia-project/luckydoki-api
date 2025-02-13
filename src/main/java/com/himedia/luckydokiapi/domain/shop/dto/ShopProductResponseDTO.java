package com.himedia.luckydokiapi.domain.shop.dto;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductResponseDTO {
    private Long shopId;
    private String shopName; // 샵 주인의 닉네임
    private List<ProductDTO.Response> productList;
}
