package com.himedia.luckydokiapi.domain.shop.entity;

import com.himedia.luckydokiapi.domain.likes.entity.ShopLike;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shop")
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;

    @Column(nullable = false, length = 100)
    private String introduction;

    @OneToOne
    @JoinColumn(name = "email")
    private Member member;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ShopLike> shopLikes = new ArrayList<>();


    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> productList = new ArrayList<>();


    public Integer shopLikesCount(ShopLike shopLike) {
        return this.shopLikes.size();
    }
}
