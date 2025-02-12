package com.himedia.luckydokiapi.domain.community.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunitySearchDTO {

    private String searchKeyword;
}
