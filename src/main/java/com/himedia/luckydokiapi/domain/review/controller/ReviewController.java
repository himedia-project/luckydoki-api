package com.himedia.luckydokiapi.domain.review.controller;


import com.himedia.luckydokiapi.domain.review.dto.ReviewRequestDTO;
import com.himedia.luckydokiapi.domain.review.dto.ReviewResponseDTO;
import com.himedia.luckydokiapi.domain.review.service.ReviewService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    //모든 리뷰 보기
    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews(@PathVariable Long productId) {
        List<ReviewResponseDTO> reviewDTOS = reviewService.findAll(productId);
        return ResponseEntity.ok(reviewDTOS);
    }

    //해당 유저의 리뷰 보기(유저 마이페이지에 넣릉거면 쓰시면 됩니당)
    @GetMapping("/member")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewByMember(@AuthenticationPrincipal MemberDTO memberDTO) {
        List<ReviewResponseDTO> reviewDTOSMember = reviewService.getReviewByMember(memberDTO.getEmail());
        return ResponseEntity.ok(reviewDTOSMember);
    }

    @PostMapping
    public ResponseEntity<String> createReview(@AuthenticationPrincipal MemberDTO memberDTO, ReviewRequestDTO reviewRequestDTO) {
        reviewService.createReview(memberDTO.getEmail(), reviewRequestDTO);
        return ResponseEntity.ok("리뷰 작성이 완료 되었습니다");
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Long> deleteReview(@AuthenticationPrincipal MemberDTO memberDTO, @PathVariable Long reviewId) {
        Long deleteReviewId = reviewService.deleteReview(memberDTO.getEmail(),reviewId);
        return ResponseEntity.ok(deleteReviewId);

    }
}
