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
    private Long id;  // 신청 고유 ID

    @Column(nullable = false, unique = true)
    private String email;  // 신청한 유저의 이메일

    @Column(nullable = false)
    private String nickName;  // 신청한 유저의 닉네임

    @Column(nullable = false)
    private boolean isApproved; // false: 승인 대기, true: 승인 완료

    /**
     * 셀러 승인 처리
     */
    public void approve() {
        this.isApproved = true;
    }
}
