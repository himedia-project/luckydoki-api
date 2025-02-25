package com.himedia.luckydokiapi.domain.community.repository;

import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.entity.CommunityTag;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {



    @Modifying
    @Query("delete from CommunityTag t where t.community = :community")
    void deleteByCommunity(@Param("community") Community community);
}
