package com.himedia.luckydokiapi.domain.order.dto;

import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "주문 아이템 dto ")
public class OrderItemDTO {
    @Schema(description = "상품 번호")
    private Long productId;
    private String productName;
    private int count;
    private int orderPrice;
    @Schema(description = "상품 할인 가격")
    private int discountPrice;
    private String image;

    // builder
    public static OrderItemDTO from(OrderItem orderItem, String image) {
        return OrderItemDTO.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .count(orderItem.getCount())
                .orderPrice(orderItem.getOrderPrice())
                .discountPrice(orderItem.getProduct().getDiscountPrice())
                .image(image)
                .build();
    }
}
