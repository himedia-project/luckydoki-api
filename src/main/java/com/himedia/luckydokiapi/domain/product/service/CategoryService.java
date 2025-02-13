package com.himedia.luckydokiapi.domain.product.service;



import com.himedia.luckydokiapi.domain.product.dto.AdminCategoriesDTO;
import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ChildCategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

public interface CategoryService {


    List<CategoryDTO> getParentCategories();

    List<ChildCategoryDTO> getChildCategories(Long categoryId);

    List<CategoryDTO> getSubCategoryList(Long mainCategoryId);

    List<CategoryDTO> getChildCategoryList(Long subCategoryId);

    List<ProductDTO.Response> getProductCategoryId(Long categoryId) ;


    List<AdminCategoriesDTO> getCategory(Long categoryId);

    default CategoryDTO entityToCategoriesDTO(Category category) {
        CategoryDTO mainCategoryDTO = CategoryDTO.builder()
                .name(category.getName())
                .logo(category.getLogo())
                .categoryId(category.getId())
                .build();
        return mainCategoryDTO;
    }

    default AdminCategoriesDTO entityToDTO(Category category) {

        AdminCategoriesDTO dto = new AdminCategoriesDTO();
        dto.setCategoryId(category.getId());
        dto.setName(category.getName());
        dto.setLogo(category.getLogo());
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            dto.setChildren(category.getChildren().stream().map(this::entityToDTO).collect(Collectors.toSet()));
        }
        return dto;
    }

    default ChildCategoryDTO entityToChildCategoryDTO(Category category) {
        return ChildCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .logo(category.getLogo())
                .build();
    }
}
