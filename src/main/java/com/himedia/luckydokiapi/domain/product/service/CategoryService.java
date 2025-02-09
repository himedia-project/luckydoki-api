package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.member.dto.AdminCategoriesDTO;
import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

public interface CategoryService {

    List<ProductDTO.Response> getProductsByChildCategoryId(Long childCategoryId) ;

    List<CategoryDTO> getCategory(Long categoryId);

//    List<ProductDTO> getCategoryProducts(Long categoryId);

    List<AdminCategoriesDTO> getAdminSubCategoryList(Long mainCategoryId);

    List<AdminCategoriesDTO> getAdminChildCategoryList(Long subCategoryId);

    List<AdminCategoriesDTO> getAdminParentCategories();

    default CategoryDTO entityToDTO(Category category) {

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getId());
        dto.setName(category.getName());
        dto.setLogo(category.getLogo());
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            dto.setChildren(category.getChildren().stream().map(this::entityToDTO).collect(Collectors.toSet()));
        }
        return dto;
    }

    default AdminCategoriesDTO entityToAdminCategoriesDTO(Category category) {
        AdminCategoriesDTO mainCategoryDTO = AdminCategoriesDTO.builder()
                .name(category.getName())
                .logo(category.getLogo())
                .categoryId(category.getId())
                .build();
        return mainCategoryDTO;
    }
}
