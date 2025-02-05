package com.himedia.luckydokiapi.domain.heart.repository.querydsl;


import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.himedia.luckydokiapi.domain.heart.entity.QHeart.heart;


@Slf4j
@RequiredArgsConstructor
public class HeartRepositoryImpl implements HeartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findProductListByMember(String email) {

        return queryFactory
                .select(heart.product)
                .from(heart)
                .where(
                        heart.product.isNotNull(),
                        heart.member.email.eq(email)
                )
                .fetch();

    }
}
