package com.himedia.luckydokiapi.domain.community.repository;

import com.himedia.luckydokiapi.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCommunityId(Long communityId);

}
