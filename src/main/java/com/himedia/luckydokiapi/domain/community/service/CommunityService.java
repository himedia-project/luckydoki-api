package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;

import java.util.ArrayList;
import java.util.List;

public interface CommunityService {
    List<CommunityResponseDTO> getAllCommunities(CommunitySearchDTO request);
    List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email);
    CommunityResponseDTO getCommunityById(Long communityId);
    CommunityResponseDTO postCommunity(String email, CommunityRequestDTO request);
    CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request);
    void deleteCommunity(Long communityId, String email);


    default CommunityResponseDTO toDTO(Community community) {
        List<String> images = new ArrayList<>(community.getImageList());
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .nickName(community.getMember().getNickName())
                .content(community.getContent())
                .imageList(images)
                .createdAt(community.getCreatedAt())
                .build();
    }

}
