package com.himedia.luckydokiapi.domain.shop.repository.querydsl;

import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.shop.dto.ShopSearchDTO;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.himedia.luckydokiapi.domain.shop.entity.QShop.shop;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShopRepositoryImpl implements ShopRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Shop> findListBy(ShopSearchDTO requestDTO) {

        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        // pageable의 sort 정보를 적용
        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(pageable.getSort());

        List<Shop> list = queryFactory
                .selectFrom(shop)
                .leftJoin(shop.member)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Shop> countQuery = queryFactory
                .selectFrom(shop)
                .leftJoin(shop.member)
                .where(
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                );


        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }

    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return null;
        }
        return shop.member.nickName.contains(searchKeyword);
    }


    /**
     * Sort 정보를 OrderSpecifier 배열로 변환
     *
     * @param sort Sort 정보
     * @return OrderSpecifier 배열
     */
    private OrderSpecifier[] createOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        new PathBuilder<>(Product.class, "shop").get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }
}
