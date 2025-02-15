package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.security.MemberDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final ProductService productService;

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDTO.Response> getProduct(@PathVariable Long id , @AuthenticationPrincipal MemberDTO memberDTO) {
        String email =  (memberDTO !=null) ? memberDTO.getEmail() : null;
        log.info("getProduct: {},email: {}", id , email);
        return ResponseEntity.ok(productService.getProduct(id , email));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO.Response>> searchProducts(ProductSearchDTO requestDTO ,@AuthenticationPrincipal MemberDTO memberDTO) {
        String email =  (memberDTO !=null) ? memberDTO.getEmail() : null;
        log.info("searchProducts: {},email :{}", requestDTO ,email);
        return ResponseEntity.ok(productService.list(requestDTO,email));
    }

    @GetMapping("/{id}/tag/list")
    public ResponseEntity<List<TagDTO>> searchProducts(@PathVariable Long id) {
        log.info("searchProductsTag: {}", id);
        return ResponseEntity.ok(productService.tagList(id));
    }


}
