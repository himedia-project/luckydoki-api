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

    //admin 에게 보여지는 카테고리 리스트
    //부모 카테고리를 클릭하면 해당 자식 카테고리들 까지 나오게 !
    @GetMapping("/{categoryId}")
    public ResponseEntity<List<CategoryDTO>> getCategories(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

}
