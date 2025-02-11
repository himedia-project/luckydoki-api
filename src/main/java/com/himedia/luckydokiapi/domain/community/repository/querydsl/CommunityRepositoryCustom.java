package com.himedia.luckydokiapi.domain.community.repository.querydsl;


import com.himedia.luckydokiapi.domain.community.DTO.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;

import java.util.List;

public interface CommunityRepositoryCustom {

    List<Community> findByDTO(CommunitySearchDTO requestDTO);


}
