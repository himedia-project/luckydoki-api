package com.himedia.luckydokiapi.domain.product.repository.querydsl;

import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.QCategory;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.himedia.luckydokiapi.domain.product.entity.QCategory.category;
import static com.himedia.luckydokiapi.domain.product.entity.QProduct.product;

@Slf4j
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findMainCategories() {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.id.isNull()) //parent 값이 비어 있으면 최상위 카테고리
                .fetch();
    }


    //최상위 카테고리 아이디로 그 아래 서브카테고리들 조회
    @Override
    public List<Category> findSubCategories(Long mainCategoryId) {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.id.eq(mainCategoryId))// 파라미터로 받은 아이디 값이 부모인 카테고리들
                .fetch();
    }

    //서브 카테고리 아이디로 그 아래 child 카테고리 들 조회
    @Override
    public List<Category> findChildCategories(Long subCategoryId) {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.id.eq(subCategoryId)) // 파라미터로 받은 아이디 값이 부모인 카테고리들
                .fetch();
    }


    @Override
    public List<Product> findListByCategory(Long categoryId) {
        return queryFactory
                .selectFrom(product)
                .where(
                        product.category.id.eq(categoryId),
                        product.approvalStatus.eq(ProductApproval.Y) // 승인된 상품만 조회
                )
                .fetch();
    }

}

