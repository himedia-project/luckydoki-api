package com.himedia.luckydokiapi.domain.product.service;




import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getCategory();

//    List<ProductDTO> getCategoryProducts(Long categoryId);

    default CategoryDTO entityToDTO(Category category) {

        return CategoryDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .build();
    }
}
