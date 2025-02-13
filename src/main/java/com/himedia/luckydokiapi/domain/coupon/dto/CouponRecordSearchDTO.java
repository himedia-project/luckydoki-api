package com.himedia.luckydokiapi.domain.coupon.dto;

import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRecordSearchDTO extends PageRequestDTO {

    private String searchKeyword;
}
