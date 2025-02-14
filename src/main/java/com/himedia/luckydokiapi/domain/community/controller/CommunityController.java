package com.himedia.luckydokiapi.domain.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.service.CommunityService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Slf4j
public class CommunityController {

    private final CommunityService communityService;
    private final CustomFileUtil customFileUtil;

    @PostMapping("/post")
    public ResponseEntity<CommunityResponseDTO> postCommunity(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @RequestBody CommunityRequestDTO requestDTO) {

        log.info("postCommunity: {}", requestDTO);
        return ResponseEntity.ok(communityService.postCommunity(memberDTO.getEmail(), requestDTO));
    }


    @GetMapping("/all")
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunities(CommunitySearchDTO request) {
        return ResponseEntity.ok(communityService.getAllCommunities(request));
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDTO> getCommunityById(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityService.getCommunityById(communityId));
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
