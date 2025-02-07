package com.himedia.luckydokiapi.domain.product.repository.querydsl;

import com.himedia.luckydokiapi.domain.product.entity.Tag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static com.himedia.luckydokiapi.domain.product.entity.QProductTag.productTag;
import static com.himedia.luckydokiapi.domain.product.entity.QTag.tag;


@Slf4j
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findByTag(Long id) {
        return queryFactory
                .select(productTag.product.id)
                .from(productTag)
                .where(productTag.tag.id.eq(id))
                .fetch();
    }



}
