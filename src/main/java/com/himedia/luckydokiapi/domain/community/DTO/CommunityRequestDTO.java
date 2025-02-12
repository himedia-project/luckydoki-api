package com.himedia.luckydokiapi.domain.community.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRequestDTO {
    private String content;
    private List<String> imageList;
    private List<Long> productIds;
}
