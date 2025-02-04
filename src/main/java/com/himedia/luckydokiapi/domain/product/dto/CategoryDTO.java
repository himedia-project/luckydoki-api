package com.himedia.luckydokiapi.domain.product.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CategoryDTO {
    private Long categoryId;
    private String name;
}
