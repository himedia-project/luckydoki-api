package com.himedia.luckydokiapi.domain.shop.dto;

import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SellerSearchDTO extends PageRequestDTO {

    private String searchKeyword;

}
