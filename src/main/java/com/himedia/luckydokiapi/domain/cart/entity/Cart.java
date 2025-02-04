package com.himedia.luckydokiapi.domain.cart.entity;


import com.himedia.luckydokiapi.domain.member.entity.Member;
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
@ToString(exclude = {"member"})
@Table(name = "cart",
        indexes = {
                @Index(name = "idx_cart_email", columnList = "email")
        })
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "email")
    private Member member;


}
