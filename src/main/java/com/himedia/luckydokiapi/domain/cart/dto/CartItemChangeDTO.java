package com.himedia.luckydokiapi.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemChangeDTO {

    private int qty;      // 장바구니상품 수량

}