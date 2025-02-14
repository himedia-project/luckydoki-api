package com.himedia.luckydokiapi.domain.community.service;

import com.himedia.luckydokiapi.domain.community.dto.CommentRequestDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommentResponseDTO;
import com.himedia.luckydokiapi.domain.community.entity.Comment;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.repository.CommentRepository;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;


    @Override
    public CommentResponseDTO postComment(String email, Long communityId, CommentRequestDTO request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음: " + email));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않음: " + communityId));

        Comment comment = Comment.builder()
                .member(member)
                .community(community)
                .content(request.getContent())
                .build();

        commentRepository.save(comment);

        return new CommentResponseDTO(comment);
    }

    // ✅ 댓글 삭제
    @Override
    public void deleteComment(String email, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않음: " + commentId));

        if (!comment.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
