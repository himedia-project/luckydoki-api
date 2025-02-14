package com.himedia.luckydokiapi.domain.community.repository;

import com.himedia.luckydokiapi.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
