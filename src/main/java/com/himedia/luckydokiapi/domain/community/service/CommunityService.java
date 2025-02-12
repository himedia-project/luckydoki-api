package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.DTO.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.DTO.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.DTO.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO> getAllCommunities(CommunitySearchDTO request) {
        return communityRepository.findByDTO(request)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityResponseDTO> getCommunitiesByMemberEmail(String email) {
        return communityRepository.findByMemberEmail(email)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommunityResponseDTO getCommunityById(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
        return toDTO(community);
    }


    public CommunityResponseDTO postCommunity(String email, CommunityRequestDTO request) {
        log.info("✅ 서비스 요청: {}", request);

        // 🔹 이메일을 통해 Member 조회
        Member member = this.getMember(email);

        Community community = Community.builder()
                .member(member)  // ✅ Member 객체 설정
                .content(request.getContent())
                .imageList(request.getImageList())
                .build();

        communityRepository.save(community);
        log.info("✅ 게시글 저장 완료! ID: {}", community.getId());

        return toDTO(community);
    }


    public CommunityResponseDTO updateCommunity(Long communityId, String email, CommunityRequestDTO request) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = this.getMember(email);

        if (!community.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        community.setContent(request.getContent());
        community.setImageList(request.getImageList());

        log.info("✅ 게시글 수정 완료: communityId={}", communityId);
        return toDTO(community);
    }


    public void deleteCommunity(Long communityId, String email) {
        log.info("✅ 게시글 삭제 요청: communityId={}, email={}", communityId, email);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        Member member = this.getMember(email);
        if (!community.getMember().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        communityRepository.delete(community);

        // 등록된 상품(중간매필테이블) 삭제
        // 1 - [1,2,3,4]
        // 1 - 1
        // 1 - 2
        // 1 - 3
        // 1 - 4
        log.info("✅ 게시글 삭제 완료: communityId={}", communityId);
    }


    private CommunityResponseDTO toDTO(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .nickName(community.getMember().getNickName()) // ✅ Member에서 닉네임 가져오기
                .content(community.getContent())
                .imageList(community.getImageList())
                .createdAt(community.getCreatedAt())
                .build();
    }

    private Member getMember(String email) {
        return memberService.getEntity(email);
    }
}
