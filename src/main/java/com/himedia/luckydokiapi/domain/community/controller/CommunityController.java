package com.himedia.luckydokiapi.domain.community.controller;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.service.CommunityService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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

    @GetMapping("/{id}/detail")
    public ResponseEntity<CommunityResponseDTO> getCommunity(@PathVariable Long id) {
        log.info("getCommunity: {}", id);
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<CommunityResponseDTO>> searchCommunities(CommunitySearchDTO requestDTO) {
        log.info("searchCommunities: {}", requestDTO);
        return ResponseEntity.ok(communityService.getAllCommunities(requestDTO));
    }

//    @PostMapping("/post")
//    public ResponseEntity<CommunityResponseDTO> postCommunity(
//            @AuthenticationPrincipal MemberDTO memberDTO,
//            @RequestBody CommunityRequestDTO requestDTO) {
//
//        log.info("postCommunity: {}", requestDTO);
//        return ResponseEntity.ok(communityService.postCommunity(memberDTO.getEmail(), requestDTO));
//    }

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


