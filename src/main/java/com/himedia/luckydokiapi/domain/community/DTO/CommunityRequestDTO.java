package com.himedia.luckydokiapi.domain.community.DTO;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class CommunityRequestDTO {
    private String email;
    private String content;
    private List<String> imageList;
}
