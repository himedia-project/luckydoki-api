package com.himedia.luckydokiapi.domain.product.controller;


import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    //최상위 카테고리 조회
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getParentCategories() {
        return ResponseEntity.ok(categoryService.getParentCategories());
    }

    //메인 카테고리 별 서브 카테고리 list
    @GetMapping("/{mainCategoryId}/sub/list")
    public ResponseEntity<List<CategoryDTO>> getSubCategory(@PathVariable Long mainCategoryId) {
        log.info("getSubCategory {}", mainCategoryId);
        return ResponseEntity.ok(categoryService.getSubCategoryList(mainCategoryId));
    }

    //서브 카테고리 별 child 카테고리 list
    @GetMapping("/{subCategoryId}/child/list")
    public ResponseEntity<List<CategoryDTO>> getChildCategory(@PathVariable Long subCategoryId) {
        log.info("getChildCategory {}", subCategoryId);
        return ResponseEntity.ok(categoryService.getChildCategoryList(subCategoryId));
    }

    // 카테고리 아이디(sub , child) 로 이에 해당하는 productList 가져오기
    @GetMapping("/{categoryId}/product/list")
    public ResponseEntity<List<ProductDTO.Response>> getProductByCategoryId(@PathVariable Long categoryId) {
        log.info("getProductByCategoryId {}", categoryId);
        return ResponseEntity.ok(categoryService.getProductCategoryId(categoryId));
    }


}
