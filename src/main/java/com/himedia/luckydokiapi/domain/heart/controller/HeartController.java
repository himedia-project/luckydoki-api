package com.himedia.luckydokiapi.domain.heart.controller;

import com.himedia.luckydokiapi.domain.heart.service.HeartService;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/heart")
@RequiredArgsConstructor
public class HeartController {
    private final HeartService heartService;


    // 찜하기/찜하기 취소 상품
    @PostMapping("/product/{productId}")
    public ResponseEntity<?> heartProduct(@PathVariable Long productId, @AuthenticationPrincipal MemberDTO memberDTO) {

        heartService.heartProduct(productId, memberDTO.getEmail());
        return ResponseEntity.ok().build();
    }


    // 해당유저의 찜목록(상품)
    @GetMapping("/product/list")
    public List<ProductResponseDTO> heartProductList(@AuthenticationPrincipal MemberDTO memberDTO) {

        return heartService.findProductListByMember(memberDTO.getEmail());
    }



}
