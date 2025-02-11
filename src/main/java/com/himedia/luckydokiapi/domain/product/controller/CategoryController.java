package com.himedia.luckydokiapi.domain.product.controller;


import com.himedia.luckydokiapi.domain.member.dto.AdminCategoriesDTO;
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



    //최하위 카테고리 아이디로 이에 해당하는 productList 가져오기
    @GetMapping("/products/{childCategoryId}")
    public ResponseEntity<List<ProductDTO.Response>> getChildCategoryById(@PathVariable Long childCategoryId) {
        return ResponseEntity.ok(categoryService.getProductsByChildCategoryId(childCategoryId));
    }
    //최상위 카테고리 조회
    @GetMapping
    public ResponseEntity <List<AdminCategoriesDTO>> getParentCategories() {
        return ResponseEntity.ok(categoryService.getAdminParentCategories());
    }

    //메인 카테고리 별 서브 카테고리 list
    @GetMapping("sub/{mainCategoryId}")
    public ResponseEntity <List<AdminCategoriesDTO>>getSubCategory(@PathVariable Long mainCategoryId) {
        return ResponseEntity.ok(categoryService.getAdminSubCategoryList(mainCategoryId));
    }
    //서브 카테고리 별 child 카테고리 list
    @GetMapping("child/{subCategoryId}")
    public ResponseEntity<List<AdminCategoriesDTO>> getChildCategory(@PathVariable Long subCategoryId) {
        return ResponseEntity.ok(categoryService.getAdminChildCategoryList(subCategoryId));
    }

}
