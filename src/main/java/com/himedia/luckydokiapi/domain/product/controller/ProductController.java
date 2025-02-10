package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDTO.Response> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO.Response>> searchProducts(ProductSearchDTO requestDTO) {
        return ResponseEntity.ok(productService.list(requestDTO));
    }

    @GetMapping("/{id}/tag/list")
    public ResponseEntity<List<TagDTO>> searchProducts(@PathVariable Long id) {
        return ResponseEntity.ok(productService.tagList(id));
    }


    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
        return fileUtil.getFile(fileName);
    }

    //TODO: memberProductController 에 상품 crud  만들기 + create 할때 seller로 권한 요청
}
