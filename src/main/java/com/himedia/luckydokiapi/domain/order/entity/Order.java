package com.himedia.luckydokiapi.domain.order.entity;


import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder // 빌더 패턴 사용
@Entity
@ToString(exclude = {"member", "orderItems"})
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    // 주문 날짜
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 주문 총 금액
    @ColumnDefault("0")
    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // order item 추가
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 주문 생성
    public static Order from(Member member, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER)
                .code(orderCodeGenerator()) // 주문 코드 생성
                .build();

        orderItems.forEach(order::addOrderItem);

        return order;
    }

    // 총합 계산
    public int getCalcTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    // 주문 취소
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        // 주문 항목 취소 로직은 필요 시 여기에 추가
    }

    /**
     * 주문 코드 생성
     * @return 주문 코드 ex. 2021070112000001
     */
    private static String orderCodeGenerator() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        long count = new AtomicLong(0).incrementAndGet();
        return String.format("%s%04d", timestamp, count);
    }

    public void changeTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
