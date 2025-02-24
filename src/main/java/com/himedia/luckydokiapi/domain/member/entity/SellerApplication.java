package com.himedia.luckydokiapi.domain.member.entity;

import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_application")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SellerApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String shopImage;

    @Column(nullable = false, length = 100)
    private String introduction;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShopApproved approved;

    private LocalDateTime approvedAt;

    public void changeApproved(ShopApproved approved) {
        this.approved = approved;
    }

    public void approve() {
        this.approved = ShopApproved.Y;
        this.approvedAt = LocalDateTime.now();
    }

    public void changeShopImage(String uploadS3File) {
        this.shopImage = uploadS3File;
    }
}