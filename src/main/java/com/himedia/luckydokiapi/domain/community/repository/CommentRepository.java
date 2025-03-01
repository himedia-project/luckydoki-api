package com.himedia.luckydokiapi.domain.community.repository;

import com.himedia.luckydokiapi.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
@Query("select c from Comment c where c.id =:id order by c.createdAt desc ")
    List<Comment> findByCommunityIdDesc(Long communityId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.member.email = :email")
    void deleteByMemberEmail(@Param("email") String email);


}
