package com.himedia.luckydokiapi.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDTO {

    private String paymentKey;        // 결제 키
    private Long cancelAmount;        // 취소 금액
    private String cancelReason;      // 취소 사유 (필수)
    private String cancelRequester;   // 취소 요청자
    private String refundBankCode;    // 환불 은행 코드 (가상계좌 결제 취소 시 필수)
    private String refundAccountNumber; // 환불 계좌번호 (가상계좌 결제 취소 시 필수)
    private String refundHolderName;   // 환불 예금주 (가상계좌 결제 취소 시 필수)

    // 부분 취소 시 필요한 정보
    private List<RefundTarget> refundTargets;  // 환불 대상 상품 정보

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundTarget {
        private String orderItemId;   // 주문 상품 ID
        private Long amount;          // 환불 금액
        private Integer quantity;     // 환불 수량
    }
}
