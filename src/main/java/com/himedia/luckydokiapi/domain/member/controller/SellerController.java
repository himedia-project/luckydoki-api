package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
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
@RequestMapping("/api/seller/product")
@RequiredArgsConstructor
public class SellerController {

    //관심사 분리 -> 멤버의 상품 등록 + shop 관련 api

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductDetail(@PathVariable Long productId) {
        ProductResponseDTO product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/list")
    public ResponseEntity<List<ProductResponseDTO>> listByMember(@AuthenticationPrincipal MemberDTO memberDTO) {
        log.info("list: {}", memberDTO);
        List<ProductResponseDTO> dto = productService.getListByMember(memberDTO.getEmail());
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<String> createMyProduct(@AuthenticationPrincipal MemberDTO memberDTO, ProductDTO dto) {
        log.info("create: {}", memberDTO, "ProductDTO :{}", dto);
        productService.createProduct(memberDTO.getEmail(), dto);
        return ResponseEntity.ok("상품 등록이 완료 되었숩니다 😀");
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Long> modifyProduct(@AuthenticationPrincipal MemberDTO memberDTO, @PathVariable Long productId, ProductDTO dto) {
        log.info("modify: {}", memberDTO);
        productService.updateProduct(memberDTO.getEmail(), dto, productId);
        return ResponseEntity.ok(dto.getId());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        log.info("delete: {}", productId);
        productService.deleteProductById(productId);
        return ResponseEntity.ok().build();
    }
    //TODO : 상품 상세 태그 , 리뷰
}
