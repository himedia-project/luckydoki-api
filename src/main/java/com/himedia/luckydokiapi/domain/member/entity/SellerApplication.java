package com.himedia.luckydokiapi.domain.member.entity;

import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String profileImage;

    @Column(nullable = false, length = 1000)
    private String introduction;

    @Column(nullable = false)
    private boolean isApproved;

    public void approve() {
        this.isApproved = true;
    }
}