package com.himedia.luckydokiapi.domain.order.dto;

import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {

    private Long productId;         // 상품 번호
    private String productName;     // 상품명
    private int orderPrice;         // 상품 주문 가격
    private String imgUrl;          // 상품 이미지 URL

    // builder
    public static OrderItemDTO from(OrderItem orderItem, String imgUrl) {
        return OrderItemDTO.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .orderPrice(orderItem.getOrderPrice())
                .imgUrl(imgUrl)
                .build();
    }
}
