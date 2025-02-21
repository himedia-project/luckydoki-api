package com.himedia.luckydokiapi.domain.coupon.entity;

import com.himedia.luckydokiapi.domain.coupon.enums.CouponRecordStatus;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"coupon", "member"})
@Table(name = "coupon_record")
public class CouponRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'UNUSED'")
    private CouponRecordStatus status;

    @Column(name = "used_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime usedDatetime;

    @Column(name = "issued_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime expiredAt;

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
    }


    public void useCoupon() {
        this.status = CouponRecordStatus.USED;
        this.usedDatetime = LocalDateTime.now();
    }
}
