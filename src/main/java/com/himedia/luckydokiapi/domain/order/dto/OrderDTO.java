package com.himedia.luckydokiapi.domain.order.dto;


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private List<@Valid CartItemDTO> cartItems; // 장바구니 상품 목록 (수량 필드 없음)
}
