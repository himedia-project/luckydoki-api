package com.himedia.luckydokiapi.domain.review.repository;

import com.himedia.luckydokiapi.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.member.email =:email")
    List<Review> findByMemberReviews(String email);
}
