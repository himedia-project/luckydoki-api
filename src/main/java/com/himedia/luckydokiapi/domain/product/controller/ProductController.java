package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RECENTLY_VIEWED_PREFIX = "recentlyViewed:";

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDTO.Response> getProduct(@PathVariable Long id, @AuthenticationPrincipal MemberDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("getProduct: {},email: {}", id, email);

        // 최근 본 상품 리스트에 추가
        if (email != null) {
            addRecentlyViewedProduct(email, id);
        }

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

    /**
     * 최근 본 상품 목록 조회
     * @param memberDTO 로그인한 사용자 정보
     * @return 최근 본 상품 목록
     */
    @GetMapping("/recently-viewed/list")
    public ResponseEntity<List<ProductDTO.Response>> getRecentlyViewedProducts(@AuthenticationPrincipal MemberDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("getRecentlyViewedProducts email: {}", email);
        String key = RECENTLY_VIEWED_PREFIX + email;
        
        // Object 타입을 Number 타입으로 변환 후 Long으로 변환
        List<Object> recentlyViewedProductIdsObj = redisTemplate.opsForList().range(key, 0, -1);
        List<Long> recentlyViewedProductIds = recentlyViewedProductIdsObj.stream()
                .map(id -> ((Number) id).longValue()) // Number로 변환 후 longValue() 사용
                .collect(Collectors.toList());

        // 상품 상세 정보 조회
        List<ProductDTO.Response> recentlyViewedProducts = productService.getProductsByIds(recentlyViewedProductIds, email);

        return ResponseEntity.ok(recentlyViewedProducts);
    }

    /**
     * 최근 본 상품 목록에 상품 추가
     * @param email 사용자 이메일
     * @param productId 상품 ID
     */
    private void addRecentlyViewedProduct(String email, Long productId) {
        String key = RECENTLY_VIEWED_PREFIX + email;
        redisTemplate.opsForList().leftPush(key, productId);
        redisTemplate.opsForList().trim(key, 0, 9); // 최근 10개만 유지
    }
}
