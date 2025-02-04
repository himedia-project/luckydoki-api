package com.himedia.luckydokiapi.domain.product.repository.querydsl;

import java.util.List;

public interface TagRepositoryCustom {

    List<Long> findByTag(Long id);
}
