package com.himedia.luckydokiapi.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String version;          // 토스페이먼츠 API 버전
    private String paymentKey;       // 결제 키
    private String orderId;          // 주문 ID
    private String orderName;        // 주문명
    private String currency;         // 통화 (KRW)
    private String method;           // 결제 수단
    private String status;           // 결제 상태 (READY, IN_PROGRESS, DONE, CANCELED, PARTIAL_CANCELED, ABORTED, EXPIRED)
    private Long totalAmount;        // 총 결제 금액
    private Long balanceAmount;      // 잔액
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime requestedAt;  // 결제 요청 시각
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime approvedAt;   // 결제 승인 시각

    private CardInfo card;           // 카드 결제 정보 (카드 결제 시)
    private VirtualAccountInfo virtualAccount;  // 가상계좌 정보 (가상계좌 결제 시)

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class CardInfo {
        private String company;      // 카드사
        private String number;       // 카드 번호
        private String installmentPlanMonths;  // 할부 개월 수
        private Boolean isInterestFree;        // 무이자 할부 여부
        private String approveNo;              // 승인 번호
        private String useCardPoint;           // 카드 포인트 사용 여부
        private String cardType;               // 카드 종류
        private String ownerType;              // 소유자 타입
        private String acquireStatus;          // 승인 상태
        private String receiptUrl;             // 영수증 URL
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class VirtualAccountInfo {
        private String accountNumber;          // 가상계좌 번호
        private String bankCode;               // 은행 코드
        private String customerName;           // 입금자명
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private OffsetDateTime dueDate;         // 입금 기한
        private Boolean refundStatus;          // 환불 상태
        private Boolean expired;               // 만료 여부
        private String settlementStatus;       // 정산 상태
    }

    // 취소 관련 정보
    private List<CancelHistory> cancels;       // 취소/환불 이력

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class CancelHistory {
        private String cancelAmount;           // 취소 금액
        private String cancelReason;           // 취소 사유
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        private OffsetDateTime canceledAt;      // 취소 시각
        private String refundableAmount;       // 환불 가능 금액
    }
}
