package com.himedia.luckydokiapi.domain.order.entity;


import com.himedia.luckydokiapi.domain.coupon.entity.Coupon;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.order.enums.OrderStatus;
import com.himedia.luckydokiapi.domain.payment.entity.Payment;
import com.himedia.luckydokiapi.domain.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@DynamicUpdate
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder // 빌더 패턴 사용
@Entity
@ToString(exclude = {"member", "orderItems", "payments"})
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    // 주문 날짜
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ORDER'")
    private OrderStatus orderStatus;

    // 주문한 상품 합계금액
    @ColumnDefault("0")
    private int productsPrice;

    // 주문 총 금액(실제 결제금액)
    @ColumnDefault("0")
    private int totalPrice;
    // 총 할인 금액
    @ColumnDefault("0")
    private int totalDiscountPrice;

    @ManyToOne(fetch = FetchType.LAZY)  // ✳️ 1개의 쿠폰으로 여러개의 주문 가능
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    // order item 추가
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 주문 생성
    public static Order from(Member member, Coupon useCoupon, List<OrderItem> orderItems, int totalDiscountPrice) {
        Order order = Order.builder()
                .member(member)
//                .coupon(useCoupon)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.ORDER)
                .code(orderCodeGenerator()) // 주문 코드 생성
                .productsPrice(getSum(orderItems))
                .totalPrice(getSum(orderItems) - totalDiscountPrice)
                .totalDiscountPrice(totalDiscountPrice)
                .build();

        orderItems.forEach(order::addOrderItem);

        return order;
    }

    private static int getSum(List<OrderItem> orderItems) {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }

    // 총합 계산
    public int getCalcProductsPrice() {
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

    /**
     * 최근 결제 상태 조회
     * @return 최근 결제 상태
     */
    public PaymentStatus getRecentPaymentStatus() {
        return payments.stream()
                .max((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .map(Payment::getStatus)
                .orElse(null);
    }

    /**
     * 최근 결제 일자 조회
     * @return 최근 결제 일자(결제 승인일자)
     */
    public LocalDateTime getRecentPaymentDate() {
        return payments.stream()
                .max((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .map(Payment::getApprovedAt)
                .orElse(null);
    }

    /**
     * 주문 상태 변경 -> 결제 완료
     */
    public void changeStatusToConfirm() {
        this.orderStatus = OrderStatus.CONFIRM;
        // 해당 order의 orderItems 의 product count 만큼 stock 감소
        orderItems.forEach(OrderItem::decreaseStock);
    }
}
