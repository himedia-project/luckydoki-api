package com.himedia.luckydokiapi.domain.coupon.entity;

import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "start_date", nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status;


    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CouponRecord> couponRecordList = new ArrayList<>();

    public void changeStatus(CouponStatus couponStatus) {
        this.status = couponStatus;
    }
}
