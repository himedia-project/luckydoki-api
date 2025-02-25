package com.himedia.luckydokiapi.domain.likes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "likes 상태 변경 값들 , 둘중 하나가 null 이여도 실행됩니다")
public class LikesRequestDTO {
    private Long productId;
    private Long shopId;
}
