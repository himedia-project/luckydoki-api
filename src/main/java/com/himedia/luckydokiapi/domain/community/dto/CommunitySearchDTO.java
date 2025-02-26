package com.himedia.luckydokiapi.domain.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커뮤니티 검색 dto , 검색 조건이 추가 될 수 있습니다")
public class CommunitySearchDTO {

    private String searchKeyword;
}
