package com.himedia.luckydokiapi.domain.member.controller;

import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.member.dto.MemberDetailDTO;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.member.dto.SellerRequestDTO;
import com.himedia.luckydokiapi.domain.member.dto.UpdateMemberDTO;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.member.service.SellerService;
import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.CategoryService;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
import com.himedia.luckydokiapi.security.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "seller - api ", description = "셀러의 권한으로 수행 할 수 있는 상품 등록 , ")
public class SellerController {

    //관심사 분리 -> 멤버의 상품 등록 + shop 관련 api

    private final MemberService memberService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SellerService sellerService;

    @Operation(summary = "셀러의 등록한 상품 디테일 조회")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDTO.Response> getProductDetail(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                                @AuthenticationPrincipal MemberDTO memberDTO,
                                                                @Parameter(description = "상품 ID", required = true)
                                                                @PathVariable Long productId) {

        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        log.info("Get product detail for productId: {} , memberDTO:{}", productId, memberDTO);
        return ResponseEntity.ok(productService.getProduct(productId, email));
    }


    @GetMapping("/product/list")
    public ResponseEntity<List<ProductDTO.Response>> listByMember(@AuthenticationPrincipal MemberDTO memberDTO) {
        log.info("list: {}", memberDTO);
        return ResponseEntity.ok(productService.getListByMember(memberDTO.getEmail()));
    }

    @PostMapping("/product")
    public ResponseEntity<Long> createMyProduct(@AuthenticationPrincipal MemberDTO memberDTO, ProductDTO.Request dto) {
        log.info("create memberDTO: {}, productDTO: {}", memberDTO, dto);
        sellerService.checkedSeller(memberDTO.getEmail());
        return ResponseEntity.ok(productService.createProduct(memberDTO.getEmail(), dto));
    }




    @Operation(
            summary = "셀러의 상품을 등록하는 api 입나다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "상품 등록에 필요한 정보를 전달합니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductDTO.Request.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "상품 등록 성공"),
            }
    )
    @PutMapping("/product/{productId}")
    public ResponseEntity<Long> modifyProduct(@AuthenticationPrincipal MemberDTO memberDTO, @PathVariable Long productId, ProductDTO.Request dto) {
        log.info("modify memberDTO: {}, productId: {}", memberDTO, productId);
        sellerService.checkedSeller(memberDTO.getEmail());
        return ResponseEntity.ok(productService.updateProduct(memberDTO.getEmail(), dto, productId));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal MemberDTO memberDTO, @PathVariable Long productId) {
        log.info("delete: {} , memberDTO:{}", productId, memberDTO);
        sellerService.checkedSeller(memberDTO.getEmail());
        productService.deleteProductById(productId);
        return ResponseEntity.ok().build();
    }
    //TODO : 상품 상세 태그 , 리뷰

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
