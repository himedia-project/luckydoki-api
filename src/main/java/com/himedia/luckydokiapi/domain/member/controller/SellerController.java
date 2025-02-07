package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    //관심사 분리 -> 멤버의 상품 등록 + shop 관련 api

    private final ProductService productService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDTO.Response> getProductDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }


    @GetMapping("/product/list")
    public ResponseEntity<List<ProductDTO.Response>> listByMember(@AuthenticationPrincipal MemberDTO memberDTO) {
        log.info("list: {}", memberDTO);
        return ResponseEntity.ok(productService.getListByMember(memberDTO.getEmail()));
    }

    @PostMapping("/product")
    public ResponseEntity<Long> createMyProduct(@AuthenticationPrincipal MemberDTO memberDTO, ProductDTO.Request dto) {
        log.info("create memberDTO: {}, productDTO: {}", memberDTO, dto);
        return ResponseEntity.ok(productService.createProduct(memberDTO.getEmail(), dto));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<Long> modifyProduct(@AuthenticationPrincipal MemberDTO memberDTO, @PathVariable Long productId, ProductDTO.Request dto) {
        log.info("modify memberDTO: {}, productId: {}", memberDTO, productId);
        return ResponseEntity.ok(productService.updateProduct(memberDTO.getEmail(), dto, productId));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        log.info("delete: {}", productId);
        productService.deleteProductById(productId);
        return ResponseEntity.ok().build();
    }
    //TODO : 상품 상세 태그 , 리뷰
}
