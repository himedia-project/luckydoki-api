package com.himedia.luckydokiapi.domain.cart.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemListIdDTO {
    private List<Long> cartItemIdList;
}
