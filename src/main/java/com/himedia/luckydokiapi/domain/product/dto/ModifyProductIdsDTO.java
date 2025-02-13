package com.himedia.luckydokiapi.domain.product.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class ModifyProductIdsDTO {
    private List<Long> productIds;
}
