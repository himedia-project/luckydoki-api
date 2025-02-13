package com.himedia.luckydokiapi.domain.shop.dto;

import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShopSearchDTO extends PageRequestDTO {

    private String searchKeyword;
}
