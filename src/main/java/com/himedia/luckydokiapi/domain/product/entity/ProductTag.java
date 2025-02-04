package com.himedia.luckydokiapi.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

// 중간테이블
@Getter
@Builder
@Entity
@ToString
@Table(name = "product_tag")
@NoArgsConstructor
@AllArgsConstructor
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static ProductTag from(Tag tag, Product product) {
        return ProductTag.builder()
                .tag(tag)
                .product(product)
                .build();
    }
}
