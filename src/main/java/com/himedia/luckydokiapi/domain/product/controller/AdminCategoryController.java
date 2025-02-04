package com.himedia.luckydokiapi.domain.product.controller;


import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.service.AdminCategoryService;
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

    private final AdminCategoryService categoryService;

    // 리스트조회, 상품 등록시, select option용
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> list() {
        List<CategoryDTO> dto = categoryService.list();
        return ResponseEntity.ok(dto);
    }

    // 카테고리 등록
    @PostMapping
    public ResponseEntity<Long> register(CategoryDTO categoryDTO) {
        log.info("register: categoryDTO {}", categoryDTO);
        Long categoryId = categoryService.register(categoryDTO);
        return ResponseEntity.ok(categoryId);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<Long> modify(@PathVariable Long id, CategoryDTO categoryDTO) {
        log.info("modify: categoryId {}, categoryDTO {}", id, categoryDTO);
        Long categoryId = categoryService.modify(id, categoryDTO);
        return ResponseEntity.ok(categoryId);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        log.info("remove: categoryId {}", id);
        categoryService.remove(id);
        return ResponseEntity.ok("success remove");
    }


}
