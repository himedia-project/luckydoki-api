package com.himedia.luckydokiapi.domain.community.controller;

import com.himedia.luckydokiapi.domain.community.dto.CommentRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommentResponseDTO;
import com.himedia.luckydokiapi.domain.community.service.CommentService;
import com.himedia.luckydokiapi.security.MemberDTO;
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
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{communityId}/comment")
    public ResponseEntity<CommentResponseDTO> postComment(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long communityId,
            @RequestBody CommentRequestDTO request) {

        log.info(" 댓글 작성 요청: communityId={}, 작성자={}, 내용={}",
                communityId, memberDTO.getEmail(), request.getContent());

        return ResponseEntity.ok(commentService.postComment(memberDTO.getEmail(), communityId, request));
    }

    //  댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long commentId) {

        log.info(" 댓글 삭제 요청: commentId={}, 요청자={}", commentId, memberDTO.getEmail());

        commentService.deleteComment(memberDTO.getEmail(), commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{communityId}/comment/list")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByCommunity(@PathVariable Long communityId) {
        log.info("댓글 목록 조회 요청: communityId={}", communityId);
        return ResponseEntity.ok(commentService.getCommentsByCommunity(communityId));
    }

}
