package com.himedia.luckydokiapi.domain.community.repository.querydsl;


import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommunityRepositoryCustom {

    List<Community> findByDTO(CommunitySearchDTO requestDTO);


    List<Community> findTop10ByOrderByLikeCountAndCommentCountDesc();

    Page<Community> findListBy(CommunitySearchDTO request);
}
