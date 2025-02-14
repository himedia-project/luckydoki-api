package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommentRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommentResponseDTO;

import java.util.List;

public interface CommentService {
    CommentResponseDTO postComment(String email, Long communityId, CommentRequestDTO request);
    void deleteComment(String email, Long commentId);
    List<CommentResponseDTO> getCommentsByCommunity(Long communityId);

}
