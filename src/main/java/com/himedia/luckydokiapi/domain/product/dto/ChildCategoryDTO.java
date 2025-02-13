package com.himedia.luckydokiapi.domain.product.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ChildCategoryDTO {
    private Long id;
    private String name;
    private String logo;
}
