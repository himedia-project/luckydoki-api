package com.himedia.luckydokiapi.domain.order.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import com.himedia.luckydokiapi.domain.order.enums.OrderStatus;
import com.himedia.luckydokiapi.domain.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistDTO {

    private Long orderId;       // 주문 아이디
    private String orderCode;   // 주문 코드
    private String email;       // 주문자 이메일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;    // 주문 날짜
    private OrderStatus orderStatus;    // 주문 상태
    private Integer productsPrice;      // 주문 상품 합계금액
    private Integer totalPrice;         // 주문 총 금액(실제 결제금액)
    private Integer totalDiscountPrice; // 총 할인 금액

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime paymentDate; // 결제 날짜

    private PaymentStatus paymentStatus; // 결제 상태

    @Builder.Default
    private List<OrderItemDTO> orderItems = new ArrayList<>();

    // order -> orderHistDTO 변환
    public static OrderHistDTO from(Order order) {
        OrderHistDTO dto = OrderHistDTO.builder()
                .orderId(order.getId())
                .orderCode(order.getCode())
                .email(order.getMember().getEmail())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .productsPrice(order.getProductsPrice())
                .totalPrice(order.getTotalPrice())
                .totalDiscountPrice(order.getTotalDiscountPrice())
                .paymentDate(order.getRecentPaymentDate())
                .paymentStatus(order.getRecentPaymentStatus())
                .build();

        // 주문 아이템을 OrderItemDTO로 변환하여 추가
        for (OrderItem item : order.getOrderItems()) {
            String image = item.getProduct().getImageList().isEmpty() ? null : item.getProduct().getImageList().get(0).getImageName();
            dto.addOrderItemDto(OrderItemDTO.from(item, image));
        }

        return dto;
    }

    // 주문 상품 리스트 추가
    public void addOrderItemDto(OrderItemDTO orderItemDto) {
        orderItems.add(orderItemDto);
    }
}

