package com.himedia.luckydokiapi.domain.community.entity;

import com.himedia.luckydokiapi.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "community_product")
@ToString(exclude = {"product", "community"})
// 중간매핑 테이블(community - product)
public class CommunityProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 정적 팩토리 메서드 패턴
     * @param community
     * @param product
     * @return
     */
    public static CommunityProduct from(Community community, Product product) {
        return CommunityProduct.builder()
                .community(community)
                .product(product)
                .build();
    }
}
