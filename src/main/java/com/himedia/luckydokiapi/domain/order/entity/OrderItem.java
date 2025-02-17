package com.himedia.luckydokiapi.domain.order.entity;


import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    private int orderPrice;     // 해당상품 주문단일 가격 -> 할인가격으로!

    private int count;          // 해당 상품 주문 수량

    /**
     * 주문 상품 생성
     *
     * @param product 주문상품
     * @return OrderItem
     */
    public static OrderItem from(Product product, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrderPrice(product.getDiscountPrice());        // 할인가격으로 변경(실제 가격임)
        orderItem.setCount(count);

        return orderItem;
    }


    /**
     * 주문 상품의 총 가격 계산
     *
     * @return 총 가격
     */
    public int getTotalPrice() {
        return orderPrice * this.count; // 주문 가격 * 주문 수량
    }


    /**
     * 주문 취소 (불필요한 로직 제거) -> 현재, 주무건당 취소 로직을 돌리고 있음!
     */
    public void cancel() {
        // 주문 취소 관련 로직을 추가할 수 있습니다.
    }
}
