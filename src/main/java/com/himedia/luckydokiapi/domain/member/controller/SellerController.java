package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.UpdateMemberDTO;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.CategoryService;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.validation.Valid;
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

    private final MemberService memberService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDTO.Response> getProductDetail(@PathVariable Long productId) {
        log.info("Get product detail for productId: {}", productId);
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


    @PostMapping("/upgrade-to-seller")
    public ResponseEntity<Long> upgradeToSeller(@AuthenticationPrincipal MemberDTO memberDTO, @Valid @RequestBody SellerRequestDTO requestDTO) {
        log.info("셀러 승급 신청 요청 memberDTO: {}, requestDTO: {}", memberDTO, requestDTO);

        return ResponseEntity.ok(memberService.upgradeToSeller(memberDTO.getEmail(), requestDTO));

    }

    @GetMapping("/me")
    public MemberDetailDTO getMyInfo(@AuthenticationPrincipal MemberDTO member) {
        return memberService.getMyInfo(member.getEmail());
    }


    @PutMapping("/me")
    public MemberDetailDTO updateMyInfo(
            @AuthenticationPrincipal MemberDTO member,
            @RequestBody UpdateMemberDTO request) {
        return memberService.updateMyInfo(member.getEmail(), request);
    }


}
