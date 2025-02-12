package com.himedia.luckydokiapi.domain.community.repository;

import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.repository.querydsl.CommunityRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long>,
        CommunityRepositoryCustom {

    @Query("SELECT c FROM Community c ORDER BY c.createdAt DESC")
    List<Community> findAllByOrderByCreatedAtDesc();

    @Query("SELECT c FROM Community c WHERE c.member.email = :email ORDER BY c.createdAt DESC")
    List<Community> findByMemberEmail(@Param("email") String email);
}
