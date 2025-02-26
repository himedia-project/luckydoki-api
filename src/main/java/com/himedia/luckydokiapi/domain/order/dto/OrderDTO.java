package com.himedia.luckydokiapi.domain.order.dto;


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "장바구니 상품 목록과 쿠폰 정보 ")
public class OrderDTO {
    @Schema(description = "쿠폰 적용 후, 쿠폰 번호")
    private Long couponId;
    @Schema(description = "장바구니 상품 목록 (수량 필드 없음)")
    private List<@Valid CartItemDTO> cartItems;
}
