package com.himedia.luckydokiapi.domain.product.service;



import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;

import java.util.List;

public interface AdminCategoryService {

    List<CategoryDTO> list();

    Long register(CategoryDTO categoryDTO);

    Long modify(Long id, CategoryDTO categoryDTO);

    void remove(Long contentId);

    default Category dtoToEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getCategoryId())
                .name(dto.getName())
                .logo(dto.getLogo())
                .build();
    }

    default CategoryDTO entityToDTO(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .logo(category.getLogo())
                .build();
    }

}
