package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.entity.Category;
import com.himedia.luckydokiapi.domain.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final AdminProductService adminProductService;
    ;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getCategory() {
        List<Category> dtoLists = categoryRepository.findCategory();
        return dtoLists.stream()
                .map(this::entityToDTO) // Product를 ProductDTO로 변환
                .toList();
    }

//    @Override
//    public List<ProductDTO> getCategoryProducts(Long categoryId) {
//        List<Product> productList = categoryRepository.findListByCategory(categoryId);
//        if (productList.isEmpty()) {
//            throw new EntityNotFoundException("해당 카테고리에 대한 상품이 없습니다.");
//        }
//
//        List<ProductDTO> productDTOList = productList.stream().map(product ->
//                        adminProductService.entityToDTO(product))
//                .collect(Collectors.toList());
//
//        return productDTOList;
//    }

}
