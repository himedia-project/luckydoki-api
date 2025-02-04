package com.himedia.luckydokiapi.domain.product.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

//    @Override
//    public List<Product> findListByCategory(Long categoryId) {
//        return queryFactory
//                .selectFrom(product)
//                .where(product.category.id.eq(categoryId))
//                .stream()
//                .toList();
//    }
}
