package com.himedia.luckydokiapi.domain.product.controller;

import com.himedia.luckydokiapi.domain.product.dto.ModifyProductIdsDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.service.AdminProductService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;
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

    private final CustomFileUtil fileUtil;
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
        return ResponseEntity.ok("success");
    }


    // 이미지 불러오기
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
        return fileUtil.getFile(fileName);

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


    //TODO : member 권한 변경 (seller)  + 상품 노출 display , approval  변경 -> 찬엽이가 만들기 ㅎㅎ
}
