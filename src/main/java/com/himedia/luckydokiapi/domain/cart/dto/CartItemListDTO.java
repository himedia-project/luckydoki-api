package com.himedia.luckydokiapi.domain.cart.dto;

import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemListDTO {

    private Long cartItemId;
    private Long productId; // 상품 ID
    private String productName; // 상품 이름
    private int qty;            // 장바구니 상품수량
    private int price; // 가격
    private int discountPrice; // 할인 가격
    private int discountRate; // 할인율
    private String imageName; // 이미지 이름
    private List<TagDTO> tagList = new ArrayList<>(); // 태그 리스트

}