package com.himedia.luckydokiapi.domain.cart.entity;


import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"cart"})
@Table(name = "cart_item", indexes = {
        @Index(name = "idx_cart_item", columnList = "cart_id"),
        @Index(name = "idx_cart_item_product", columnList = "product_id, cart_id")
})
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Integer price;

    private Integer discountPrice;

    private Integer discountRate;


    public static CartItem from(Product product, Cart cart) {
        return CartItem.builder()
                .product(product)
                .cart(cart)
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .discountRate(product.getDiscountRate())
                .build();
    }


}
