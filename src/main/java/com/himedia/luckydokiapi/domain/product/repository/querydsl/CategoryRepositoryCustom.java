package com.himedia.luckydokiapi.domain.product.repository.querydsl;

import com.himedia.luckydokiapi.domain.product.entity.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findSubCategories(Long mainCategoryId);

    List<Category> findChildCategories(Long subCategoryId);
//    List<Product> findListByCategory(Long categoryId);

    List<Category> findMainCategories();
}
