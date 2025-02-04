package com.himedia.luckydokiapi.domain.product.repository.querydsl;


import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.enums.ProductMdPick;
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

import java.util.List;

import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;
import static com.himedia.luckydokiapi.domain.product.entity.QProductImage.productImage;

@Slf4j
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Product> findListBy(ProductRequestDTO requestDTO) {


        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        // pageable의 sort 정보를 적용
        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(pageable.getSort());

        List<Product> list = queryFactory
                .select(product)
                .from(product)
                .leftJoin(product.imageList, productImage).on(productImage.ord.eq(0))
                .where(
                        product.delFlag.eq(false),
                        containsSearchKeyword(requestDTO.getSearchKeyword()),
                        eqCategoryId(requestDTO.getCategoryId())
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // pageable의 sort 정보를 querydsl에 적용

        JPAQuery<Product> countQuery = queryFactory
                .select(product)
                .from(product)
                .leftJoin(product.imageList, productImage).on(productImage.ord.eq(0))
                .where(
                        product.delFlag.eq(false),
                        containsSearchKeyword(requestDTO.getSearchKeyword()),
                        eqCategoryId(requestDTO.getCategoryId())
                );

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchCount);
    }

    @Override
    public List<Product> findByDTO(ProductRequestDTO requestDTO) {

        return queryFactory
                .selectFrom(product)
                .where(
                        product.delFlag.eq(false),
                        eqCategory(requestDTO.getCategoryId()),
                        eqMdPick(requestDTO.getMdPick()),
                        containsSearchKeyword(requestDTO.getSearchKeyword())
                )
                .orderBy(product.id.desc())
                .fetch();
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
                        new PathBuilder<>(Product.class, "product").get(order.getProperty())
                ))
                .toArray(OrderSpecifier[]::new);
    }

    private BooleanExpression eqCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return product.category.id.eq(categoryId);
    }

    private BooleanExpression eqMdPick(ProductMdPick mdPick) {
        if (mdPick == null) {
            return null;
        }
        return product.mdPick.eq(mdPick);
    }


    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        return product.name.contains(searchKeyword)
                .or(product.category.name.contains(searchKeyword));

    }


    private BooleanExpression eqCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return product.category.id.eq(categoryId);
    }

}
