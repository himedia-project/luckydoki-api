package com.himedia.luckydokiapi.domain.community.controller;

import com.himedia.luckydokiapi.domain.community.dto.CommunityRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.service.CommunityService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "community - api ", description = "커뮤니티 글쓰기/리스트/상세 조회 api")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/detail/{id}")
    @Operation(summary = "커뮤니티 글 상세보기", description = "id 값으로 커뮤니티 글을 상세 조회 합니다 ")
    public ResponseEntity<CommunityResponseDTO> getCommunity(@Parameter(description = "커뮤니티 글 id") @PathVariable Long id) {
        log.info("getCommunity: {}", id);
        return ResponseEntity.ok(communityService.getCommunityById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "커뮤니티 글 검색", description = "dto 속 키워드에 해당하는 커뮤니티 게사글을 보여줍니다 ")
    public ResponseEntity<PageResponseDTO<CommunityResponseDTO>> searchCommunities(CommunitySearchDTO requestDTO, @Parameter(description = "인증된 사용자 정보", hidden = true)
    @AuthenticationPrincipal MemberDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("searchCommunities requestDTO: {}, email: {}", requestDTO, email);
        return ResponseEntity.ok(communityService.list(requestDTO, email));
    }

    @PostMapping
    @Operation(summary = "커뮤니티 글 쓰기", description = "커뮤니티 생성 요청 dto 를 저장하여 새 글을 등록합니다   ")
    public ResponseEntity<String> postCommunity(
            @Parameter(description = "인증된 사용자 정보", hidden = true)
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
            @Parameter(description = "인증된 사용자 정보", hidden = true)
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long id) {

        log.info("deleteCommunity: {}", id);
        communityService.deleteCommunity(id, memberDTO.getEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/{email}")
    public ResponseEntity<List<CommunityResponseDTO>> getCommunitiesByMember(@Parameter(description = "사용자 이메일")@PathVariable String email) {
        return ResponseEntity.ok(communityService.getCommunitiesByMemberEmail(email));
    }
}


