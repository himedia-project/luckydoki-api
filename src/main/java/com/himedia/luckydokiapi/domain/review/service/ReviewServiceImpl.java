package com.himedia.luckydokiapi.domain.review.service;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.review.dto.ReviewRequestDTO;
import com.himedia.luckydokiapi.domain.review.dto.ReviewResponseDTO;
import com.himedia.luckydokiapi.domain.review.entity.Review;
import com.himedia.luckydokiapi.domain.review.repository.ReviewRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.util.file.CustomFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {


    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CustomFileService customFileService;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> findAll(Long productId) {
        List<ReviewResponseDTO> reviewResponseDTOS = reviewRepository.findByProductId(productId).stream().map(this::entityToDTO).toList();
        return reviewResponseDTOS;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getReviewByMember(String email) {
        Member member = this.getMember(email);
        List<ReviewResponseDTO> reviewListDTOByMember = reviewRepository.findByMemberReviews(member.getEmail())
                .stream().map(this::entityToDTO).toList();
        return reviewListDTOByMember;
    }

    @Override
    public void createReview(String email, ReviewRequestDTO reviewRequestDTO) {
        Member member = this.getMember(email);
        Product product = this.getProduct(reviewRequestDTO.getProductId());
        Shop shop = product.getShop();

        // 만약 리뷰 이미지가 있으면 s3에 업로드
        String reviewImage = null;
        if (reviewRequestDTO.getImage() != null && !reviewRequestDTO.getImage().isEmpty()) {
            reviewImage = customFileService.uploadToThumbnailS3File(reviewRequestDTO.getImage());
        }
        Review review = dtoToEntity(reviewRequestDTO, member, shop, product, reviewImage);
        reviewRepository.save(review);

    }

    @Override
    public void deleteByProduct(Product product) {
        List<Review> reviews = reviewRepository.findByProductId(product.getId());
        for (Review review : reviews) {
            customFileService.deleteS3File(review.getImage());
            reviewRepository.delete(review);
        }
    }

    @Override
    public Long deleteReview(String email, Long reviewId) {
        Member member = this.getMember(email);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 review 입니다"));
        if (!review.getMember().getEmail().equals(member.getEmail())) {
            throw new RuntimeException("리뷰 작성자 본인만 삭제 가능합니다");
        }
        customFileService.deleteS3File(review.getImage());
        reviewRepository.delete(review);
        return review.getId();
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 product 입니다"));
    }

    private Shop getShop(String email) {
        return shopRepository.findByMemberEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 shop 입니다"));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다"));
    }
}
