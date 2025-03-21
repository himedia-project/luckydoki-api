package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.dto.ValidateCountRequestDTO;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.security.MemberDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDTO.Response> getProduct(@PathVariable Long id, @AuthenticationPrincipal MemberDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("getProduct: {},email: {}", id, email);
        return ResponseEntity.ok(productService.getProduct(id, email));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO.Response>> searchProducts(ProductSearchDTO requestDTO, @AuthenticationPrincipal MemberDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("searchProducts: {},email :{}", requestDTO, email);
        return ResponseEntity.ok(productService.list(requestDTO, email));
    }

    @GetMapping("/recommend/list")
    public ResponseEntity<List<ProductDTO.Response>> searchRecommendProducts(ProductDTO.Request request, @AuthenticationPrincipal MemberDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("searchProducts request: {},email :{}", request, email);
        return ResponseEntity.ok(productService.recommendList(request, email));
    }


    @GetMapping("/{id}/tag/list")
    public ResponseEntity<List<TagDTO>> searchProducts(@PathVariable Long id) {
        log.info("searchProductsTag: {}", id);
        return ResponseEntity.ok(productService.tagList(id));
    }

    @GetMapping("/{id}/validate/count")
    public ResponseEntity<String> validateProductCount(@PathVariable Long id, @RequestParam Integer count) {
        log.info("validateProductCount id: {}, count: {}", id, count);
        productService.validateProductCount(id, count);
        return ResponseEntity.ok("product validate count id: " + id);
    }

    @GetMapping("/recent-changes")
    public ResponseEntity<List<Long>> getRecentlyChangedProducts(@RequestParam String since) {
        log.info("getRecentlyChangedProducts since: {}", since);
        LocalDateTime fromTime = LocalDateTime.parse(since, DateTimeFormatter.ISO_DATE_TIME);
        // ex. since=2021-08-01T00:00:00 -> 2021-08-01 00:00:00
        return ResponseEntity.ok(productService.getRecentlyChangedProducts(fromTime));
    }

    @GetMapping("/recent-additions")
    public ResponseEntity<List<Long>> getRecentlyAddedProducts(@RequestParam String since) {
        log.info("getRecentlyAddedProducts since: {}", since);
        LocalDateTime fromTime = LocalDateTime.parse(since, DateTimeFormatter.ISO_DATE_TIME);
        return ResponseEntity.ok(productService.getRecentlyAddedProducts(fromTime));
    }
}
