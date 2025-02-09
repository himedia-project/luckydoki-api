package com.himedia.luckydokiapi.domain.product.controller;


import com.himedia.luckydokiapi.domain.member.dto.AdminCategoriesDTO;
import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.AdminCategoryService;
import com.himedia.luckydokiapi.domain.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/product/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;
    private final CategoryService categoryService;

    // 리스트조회, 상품 등록시, select option용
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> list() {
        List<CategoryDTO> dto = adminCategoryService.list();
        return ResponseEntity.ok(dto);
    }

    // 카테고리 등록
    @PostMapping
    public ResponseEntity<Long> register(CategoryDTO categoryDTO) {
        log.info("register: categoryDTO {}", categoryDTO);
        Long categoryId = adminCategoryService.register(categoryDTO);
        return ResponseEntity.ok(categoryId);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<Long> modify(@PathVariable Long id, CategoryDTO categoryDTO) {
        log.info("modify: categoryId {}, categoryDTO {}", id, categoryDTO);
        Long categoryId = adminCategoryService.modify(id, categoryDTO);
        return ResponseEntity.ok(categoryId);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        log.info("remove: categoryId {}", id);
        adminCategoryService.remove(id);
        return ResponseEntity.ok("success remove");
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
