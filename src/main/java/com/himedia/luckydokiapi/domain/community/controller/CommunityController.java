package com.himedia.luckydokiapi.domain.community.controller;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.service.CommunityService;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("/all")
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunities(CommunitySearchDTO request) {
        return ResponseEntity.ok(communityService.getAllCommunities(request));
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDTO> getCommunityById(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityService.getCommunityById(communityId));
    }

    @PostMapping("/post")
    public ResponseEntity<?> postCommunity(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @Valid @RequestBody CommunityRequestDTO request) {
        log.info("postCommunity memberDTO: {}, request: {}", memberDTO, request);
        return ResponseEntity.ok(communityService.postCommunity(memberDTO.getEmail(), request));
    }


    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDTO> updateCommunity(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long communityId,
            @Valid @RequestBody CommunityRequestDTO request) {
        log.info("updateCommunity communityId: {}, memberDTO: {}, request: {}", communityId, memberDTO, request);
        return ResponseEntity.ok(communityService.updateCommunity(communityId, memberDTO.getEmail(), request));
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> deleteCommunity(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long communityId) {
        communityService.deleteCommunity(communityId, memberDTO.getEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/{email}")
    public ResponseEntity<List<CommunityResponseDTO>> getCommunitiesByMember(@PathVariable String email) {
        return ResponseEntity.ok(communityService.getCommunitiesByMemberEmail(email));
    }

}
