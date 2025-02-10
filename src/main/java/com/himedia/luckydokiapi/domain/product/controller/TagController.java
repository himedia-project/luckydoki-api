package com.himedia.luckydokiapi.domain.product.controller;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.TagService;
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
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/{id}/product/list")
    public ResponseEntity<List<ProductDTO.Response>> list(@PathVariable Long id) {
        List<ProductDTO.Response> productDTOs = tagService.list(id);
        return ResponseEntity.ok(productDTOs);
    }
}
