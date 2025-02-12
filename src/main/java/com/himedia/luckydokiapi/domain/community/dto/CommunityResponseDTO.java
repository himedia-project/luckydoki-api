package com.himedia.luckydokiapi.domain.community.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class CommunityResponseDTO {
    private Long id;
    private String nickName;
    private String content;
    private List<String> imageList;
    private LocalDateTime createdAt;
}
