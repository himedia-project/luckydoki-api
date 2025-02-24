package com.himedia.luckydokiapi.domain.product.dto;

import com.himedia.luckydokiapi.domain.product.entity.Tag;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TagDTO {
    private Long id;
    private String name;


    public static TagDTO from(Tag tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
