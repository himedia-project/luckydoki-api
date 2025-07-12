package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.product.dto.ModifyProductIdsDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.service.AdminProductService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final CustomFileService fileService;
    private final AdminProductService productService;

    // 리스트조회
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ProductDTO.Response>> list(ProductSearchDTO requestDTO) {
        log.info("list: {}", requestDTO);
        PageResponseDTO<ProductDTO.Response> dto = productService.list(requestDTO);
        return ResponseEntity.ok(dto);
    }

    // 상세조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO.Response> read(Long id) {
        log.info("read: {}", id);
        return ResponseEntity.ok(productService.getOne(id));
    }


    // 등록
    @PostMapping
    public ResponseEntity<Long> register(ProductDTO.Request productDTO) {
        log.info("register: {}", productDTO);
        return ResponseEntity.ok(productService.register(productDTO));
    }


    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Long> modify(@PathVariable Long id, ProductDTO.Request productDTO) {
        log.info("modify: {}, {}", id, productDTO);
        return ResponseEntity.ok(productService.modify(id, productDTO));
    }


    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id) {
        log.info("remove id: {}", id);
        productService.remove(id);
        return ResponseEntity.ok("delete success productId: " + id);
    }

    // 전체 삭제
    @DeleteMapping("/all")
    public ResponseEntity<String> removeAll(@RequestBody ModifyProductIdsDTO modifyProductIdsDTO) {
        log.info("removeAll: {}", modifyProductIdsDTO);
        productService.removeAll(modifyProductIdsDTO.getProductIds());
        return ResponseEntity.ok("delete success productIds: " + modifyProductIdsDTO.getProductIds());
    }


    // 이미지 불러오기
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
        return fileService.getFile(fileName);

    }

    @PatchMapping("/is-new")
    public ResponseEntity<?> changeIsNew(@RequestBody ModifyProductIdsDTO modifyProductIdsDTO) {
        log.info("isNew: {}", modifyProductIdsDTO);
        productService.modifyProductIsNew(modifyProductIdsDTO.getProductIds());
        return ResponseEntity.ok("변경 완료 productIds: " + modifyProductIdsDTO.getProductIds());
    }

    @PatchMapping("/best")
    public ResponseEntity<?> changeBest(@RequestBody ModifyProductIdsDTO modifyProductIdsDTO) {
        log.info("best: {}", modifyProductIdsDTO);
        productService.modifyProductBest(modifyProductIdsDTO.getProductIds());
        return ResponseEntity.ok("변경 완료 productIds: " + modifyProductIdsDTO.getProductIds());
    }

    @PutMapping("/approve/all")
    public ResponseEntity<String> approveProductAll(@RequestBody ModifyProductIdsDTO modifyProductIdsDTO) {
        log.info("approveProducts: {}", modifyProductIdsDTO);
        productService.approveProductAll(modifyProductIdsDTO.getProductIds());
        return ResponseEntity.ok("상품 승인 완료. productIds: " + modifyProductIdsDTO.getProductIds());
    }


    // 승인
    @PutMapping("/approve")
    public ResponseEntity<String> approveProduct(@RequestBody ModifyProductIdsDTO requestDTO) {
        log.info("approveProduct requestDTO: {}", requestDTO);
        productService.approveProduct(requestDTO.getProductIds());
        return ResponseEntity.ok("상품 승인 완료. productId: " + requestDTO.getProductIds());
    }

    // 승인신청 목록 조회
    @GetMapping("/approve/list")
    public ResponseEntity<List<ProductDTO.Response>> getProductsByApprovalStatus(
            @RequestParam(required = false, defaultValue = "N") String status) {
        log.info("getProductsByApprovalStatus: {}", status);
        List<ProductDTO.Response> products = productService.getProductsByApprovalStatus(ProductApproval.valueOf(status));
        return ResponseEntity.ok(products);
    }

    // 상품 복사
    @PostMapping("/{id}/copy")
    public ResponseEntity<Long> copyProduct(@PathVariable Long id) {
        log.info("상품 복사 요청: productId={}", id);
        Long copiedProductId = productService.copyProduct(id);
        return ResponseEntity.ok(copiedProductId);
    }

}
