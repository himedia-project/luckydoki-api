package com.himedia.luckydokiapi.domain.product.dto;


import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductEvent;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
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
public class ProductSearchDTO extends PageRequestDTO {
//검색용 dto
    private Long id;
    private Long categoryId;
    private String releaseDate;
    private String searchKeyword;
    private ProductBest best;
    private ProductEvent event;
    private ProductIsNew isNew;
    private Long shopId;
    private Integer minPrice; // 최소가격
    private Integer maxPrice; //최대가격

    // 상품 태그 문자열 리스트 "액션,스포츠,연애"
    private List<String> tagStrList;
}
