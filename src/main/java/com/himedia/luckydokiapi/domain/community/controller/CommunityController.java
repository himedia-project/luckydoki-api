package com.himedia.luckydokiapi.domain.community.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CustomFileUtil fileUtil;
    private final CommunityService communityService;

    @GetMapping("/detail/{id}")
    public ResponseEntity<CommunityResponseDTO> getCommunity(@PathVariable Long id) {
        log.info("getCommunity: {}", id);
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<CommunityResponseDTO>> searchCommunities(CommunitySearchDTO requestDTO) {
        log.info("searchCommunities: {}", requestDTO);
        return ResponseEntity.ok(communityService.getAllCommunities(requestDTO));
    }

    @PostMapping
    public ResponseEntity<String> postCommunity(
            @AuthenticationPrincipal MemberDTO memberDTO,
            CommunityRequestDTO requestDTO) {

        log.info("postCommunity: {}", requestDTO);
        Long result = communityService.postCommunity(memberDTO.getEmail(), requestDTO);
        return ResponseEntity.ok("게시글 등록 success community id: " + result);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<CommunityResponseDTO> updateCommunity(
//            @AuthenticationPrincipal MemberDTO memberDTO,
//            @PathVariable Long id,
//            @RequestBody CommunityRequestDTO requestDTO) {
//
//        log.info("updateCommunity: {}, request: {}", id, requestDTO);
//        return ResponseEntity.ok(communityService.updateCommunity(id, memberDTO.getEmail(), requestDTO));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long id) {

        log.info("deleteCommunity: {}", id);
        communityService.deleteCommunity(id, memberDTO.getEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/{email}")
    public ResponseEntity<List<CommunityResponseDTO>> getCommunitiesByMember(@PathVariable String email) {
        return ResponseEntity.ok(communityService.getCommunitiesByMemberEmail(email));
    }
}


