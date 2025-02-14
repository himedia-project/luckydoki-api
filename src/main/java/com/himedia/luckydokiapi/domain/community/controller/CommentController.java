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

@Slf4j
@RestController
@RequestMapping("/api/community/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 작성
    @PostMapping("/{communityId}")
    public ResponseEntity<CommentResponseDTO> postComment(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long communityId,
            @RequestBody CommentRequestDTO request) {

        log.info("📌 댓글 작성 요청: communityId={}, 작성자={}, 내용={}",
                communityId, memberDTO.getEmail(), request.getContent());

        return ResponseEntity.ok(commentService.postComment(memberDTO.getEmail(), communityId, request));
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal MemberDTO memberDTO,
            @PathVariable Long commentId) {

        log.info("📌 댓글 삭제 요청: commentId={}, 요청자={}", commentId, memberDTO.getEmail());

        commentService.deleteComment(memberDTO.getEmail(), commentId);
        return ResponseEntity.noContent().build();
    }
}
