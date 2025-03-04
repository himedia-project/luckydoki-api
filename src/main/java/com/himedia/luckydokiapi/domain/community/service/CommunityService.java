package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface CommunityService {
    CommunityResponseDTO getCommunityById(Long communityId);

    PageResponseDTO<CommunityResponseDTO> list(CommunitySearchDTO request, String email);

    List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email);

    Long postCommunity(String email, CommunityRequestDTO request);

    //    CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request);
    void deleteCommunity(Long communityId, String email);


    default Community dtoToEntity(CommunityRequestDTO request, Member member) {

        Community community = Community.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .build();


        // 업로드가 처리가 끝난 파일들의 이름 리스트
        List<String> uploadFileNames = request.getUploadFileNames();

        // 업로드된 파일들의 이름을 CommunityImage 엔티티로 변환
        uploadFileNames.forEach(community::addImageString);

        return community;
    }

}
