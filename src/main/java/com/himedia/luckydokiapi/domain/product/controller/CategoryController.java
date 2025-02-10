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

    //member 에게 보여지는 카테고리 리스트
    //부모 카테고리를 클릭하면 해당 자식 카테고리들 까지 나오게 !
    @GetMapping("/{categoryId}")
    public ResponseEntity<List<CategoryDTO>> getCategories(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    //최하위 카테고리 아이디로 이에 해당하는 productList 가져오기
    @GetMapping("/products/{childCategoryId}")
    public ResponseEntity<List<ProductDTO.Response>> getChildCategoryById(@PathVariable Long childCategoryId) {
        return ResponseEntity.ok(categoryService.getProductsByChildCategoryId(childCategoryId));
    }
}
