package com.himedia.luckydokiapi.domain.product.repository.querydsl;


import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.QCategory;
import com.himedia.luckydokiapi.domain.product.entity.QCategoryBridge;
import com.himedia.luckydokiapi.domain.product.entity.QProduct;
import com.himedia.luckydokiapi.domain.product.enums.LastType;
import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductEvent;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.himedia.luckydokiapi.domain.product.entity.QCategory.category;
import static com.himedia.luckydokiapi.domain.product.entity.QCategoryBridge.categoryBridge;
import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;
import static com.himedia.luckydokiapi.domain.product.entity.QProductImage.productImage;
import static com.himedia.luckydokiapi.domain.product.enums.LastType.N;
import static com.himedia.luckydokiapi.domain.product.enums.LastType.Y;
import static com.himedia.luckydokiapi.domain.shop.entity.QShop.shop;

@Slf4j
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Product> findListBy(ProductSearchDTO requestDTO) {
//admin 용

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
                        eqCategoryId(requestDTO.getCategoryId()),
                        eqIsNew(requestDTO.getIsNew()),
                        eqBest(requestDTO.getBest()),
                        eqEvent(requestDTO.getEvent()),
                        eqShopId(requestDTO.getShopId()),
                        betweenPrice(requestDTO.getMinPrice(), requestDTO.getMaxPrice())
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

    //새로 추가된 옵션들도  (enums ) 검색 옵ㄱ션추가
    //member 용
    @Override
    public List<Product> findByDTO(ProductSearchDTO requestDTO) {

        return queryFactory
                .selectFrom(product)
                .where(
                        product.delFlag.eq(false),
                        eqCategory(requestDTO.getCategoryId()),
                        eqIsNew(requestDTO.getIsNew()),
                        containsSearchKeyword(requestDTO.getSearchKeyword()),
                        eqBest(requestDTO.getBest()),
                        eqEvent(requestDTO.getEvent()),
                        eqShopId(requestDTO.getShopId()),
                        betweenPrice(requestDTO.getMinPrice(), requestDTO.getMaxPrice()) //가격 범위 검색
                )
                .orderBy(product.id.desc())
                .fetch();
    }

    @Override
    public List<Product> findProductByShopMemberEmail(String email) {
        return queryFactory
                .select(product)
                .from(product)
                .leftJoin(product.imageList, productImage).on(productImage.ord.eq(0))
                .join(product.shop, shop)
                .join(shop.member)
                .where(shop.member.email.eq(email))
                .fetch();
    }

    //카테고리 로 조건에 맞는 해당 프로덕트 들을 조회
    @Override
    public List<Product> findByProductCategoryId(Long categoryId) {

        return queryFactory
                .selectFrom(product)
                .distinct()
                .join(product.categoryBridges, categoryBridge)
                .join(categoryBridge.category, category)
                .where(eqChildCategoryId(categoryId).or
                        (eqSubCategoryId(categoryId)))
                .fetch();
        //파라미터로 받은 카테고리 아이디가 child 카테고리 일때 (최하위 카테고리 아이디로 product 조회)
    }

    private BooleanExpression eqChildCategoryId(Long childCategoryId) {
        if (childCategoryId == null) {
            return null;
        }
        return category.parent.isNotNull().and(
                category.id.eq(childCategoryId)).and(
                category.lastType.eq(Y));
    }


    private BooleanExpression eqSubCategoryId(Long subCategoryId) {
        if (subCategoryId == null) {
            return null;
        }
        return category.parent.isNotNull().and(
                category.parent.id.eq(subCategoryId)
        );


    }


    private BooleanExpression eqCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return product.category.id.eq(categoryId);
    }


    @Override
    public List<Product> findByIdList(List<Long> idList) {
        return queryFactory.selectFrom(product)
                .where(product.id.in(idList))
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


    private BooleanExpression eqIsNew(ProductIsNew isNew) {
        if (isNew == null) {
            return null;
        }
        return product.isNew.eq(isNew);
    }

    private BooleanExpression eqBest(ProductBest best) {
        if (best == null) {
            return null;
        }
        return product.best.eq(best);
    }

    private BooleanExpression eqEvent(ProductEvent event) {
        if (event == null) {
            return null;
        }
        return product.event.eq(event);
    }

    private BooleanExpression eqShopId(Long shopId) {
        if (shopId == null) {
            return null;
        }
        return product.shop.id.eq(shopId);
    }

    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        return product.name.contains(searchKeyword)
                .or(product.category.name.contains(searchKeyword));

    }

    //가격 범위 검색
    private BooleanExpression betweenPrice(Integer minPrice, Integer maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice != null) {
            return product.price.loe(maxPrice);
        }
        if (maxPrice != null) {
            return product.price.goe(minPrice);
        }
        return product.price.between(minPrice, maxPrice);
    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return product.category.id.eq(categoryId);
    }

    private BooleanExpression eqShopMember(String email) {
        if (email == null) {
            return null;
        }
        return product.shop.member.email.eq(email); // product - shop - member - email 연관관계

    }

}
