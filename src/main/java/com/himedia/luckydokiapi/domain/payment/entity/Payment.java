package com.himedia.luckydokiapi.domain.payment.entity;


import com.himedia.luckydokiapi.domain.payment.enums.PaymentStatus;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;        // 주문 ID
    private String paymentKey;     // 결제 키
    private Long amount;           // 결제 금액

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'READY'")
    private PaymentStatus status;         // 결제 상태 (READY, DONE, CANCELED)

    private String method;         // 결제 수단 (카드, 현금, 휴대폰, 계좌이체 등)

    // 결제 요청 시각
    private LocalDateTime requestedAt;

    // 결제 승인 시각
    private LocalDateTime approvedAt;


}
