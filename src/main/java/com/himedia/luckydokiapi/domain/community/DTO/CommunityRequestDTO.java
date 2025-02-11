package com.himedia.luckydokiapi.domain.community.DTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class CommunityRequestDTO {

    private String content;
    private List<String> imageList;
}
