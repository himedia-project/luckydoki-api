package com.himedia.luckydokiapi.domain.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRecordResponseDTO {

    private Long id;                    // 쿠폰record id

    private String name;                // 쿠폰명
    private String code;                // 쿠폰 코드

    private String email;               // 해당 쿠폰을 발급받은 유저이메일

    private boolean used;               // 쿠폰 사용여부

    private long validPeriod;           // 발급일로부터 유효기간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime usedDateTime; // 쿠폰 사용일시
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime issuedAt;     // 쿠폰 발급일시

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime expiredAt;     // 쿠폰 만료일시


    public static CouponRecordResponseDTO from(CouponRecord couponRecord) {
        return CouponRecordResponseDTO.builder()
                .id(couponRecord.getId())
                .name(couponRecord.getCoupon().getName())
                .code(couponRecord.getCoupon().getCode())
                .email(couponRecord.getMember().getEmail())
                .issuedAt(couponRecord.getIssuedAt())
                .expiredAt(couponRecord.getExpiredAt())
                .used(couponRecord.getUsedDatetime() != null)
                .usedDateTime(couponRecord.getUsedDatetime())
                // 쿠폰 유효기간 계산
                .validPeriod(ChronoUnit.DAYS.between(couponRecord.getCoupon().getStartDate(), couponRecord.getCoupon().getEndDate()) + 1)
                .build();
    }
}
