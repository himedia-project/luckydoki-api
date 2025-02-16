package com.himedia.luckydokiapi.domain.review.service;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.review.dto.ReviewRequestDTO;
import com.himedia.luckydokiapi.domain.review.dto.ReviewResponseDTO;
import com.himedia.luckydokiapi.domain.review.entity.Review;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDTO> findAll(Long productId);

    List<ReviewResponseDTO> getReviewByMember(String email);

    default ReviewResponseDTO entityToDTO(Review review) {

        ReviewResponseDTO reviewResponseDTO = ReviewResponseDTO.builder()
                .nickname(review.getMember().getNickName())
                .rating(review.getRating())
                .content(review.getContent())
                .shopId(review.getShop().getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .imageName(review.getImage())
                .shopImage(review.getShop().getImage())
                .createdAt(review.getCreatedAt())
                .shopImage(review.getShop().getImage())
                .build();
        return reviewResponseDTO;

    }

    void createReview(String email, ReviewRequestDTO reviewRequestDTO);

    void deleteByProduct(Product product);


    default Review dtoToEntity(ReviewRequestDTO reviewRequestDTO, Member member, Shop shop, Product product, String image) {
        Review review = Review.builder()
                .rating(reviewRequestDTO.getRating())
                .content(reviewRequestDTO.getContent())
                .shop(shop)
                .member(member)
                .product(product)
                .image(image != null && !image.isEmpty() ? image : null)
                .build();
        return review;
    }

    Long deleteReview(String email, Long reviewId);
}