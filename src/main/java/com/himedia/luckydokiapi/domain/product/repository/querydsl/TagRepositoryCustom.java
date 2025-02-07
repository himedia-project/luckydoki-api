package com.himedia.luckydokiapi.domain.product.repository.querydsl;

import com.himedia.luckydokiapi.domain.product.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepositoryCustom {

    List<Long> findByTag(Long id);

}
