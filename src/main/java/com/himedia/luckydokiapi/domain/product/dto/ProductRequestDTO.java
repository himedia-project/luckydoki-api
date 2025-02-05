package com.himedia.luckydokiapi.domain.product.dto;


import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import com.himedia.luckydokiapi.domain.product.enums.ProductNew;
import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class ProductRequestDTO extends PageRequestDTO {

    private Long id;
    private Long categoryId;
    private ProductIsNew mdPick;
    private String releaseDate;
    private ProductNew isNew;
    private String searchKeyword;

    // 상품 태그 문자열 리스트 "액션,스포츠,연애"
    private List<String> tagStrList;
}
