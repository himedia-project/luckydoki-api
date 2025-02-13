package com.himedia.luckydokiapi.domain.shop.controller;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.SellerSearchDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopResponseDTO;
import com.himedia.luckydokiapi.domain.shop.dto.ShopSearchDTO;
import com.himedia.luckydokiapi.domain.shop.service.AdminShopService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/admin/shop")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    /**
     * 샵 리스트 조회
     */
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ShopResponseDTO>> list(ShopSearchDTO request) {
        log.info("list request: {}", request);
        return ResponseEntity.ok(adminShopService.list(request));
    }

    /**
     *  셀러 신청 승인 API
     */
    @PostMapping("/approve-seller/{applicationId}")
    public ResponseEntity<?> approve(@PathVariable Long applicationId) {
        log.info("approve applicationId: {}", applicationId);
        return ResponseEntity.ok(adminShopService.approveSeller(applicationId));
    }

    /**
     * 승인되지 않은 셀러 신청 목록 조회
     */
    @GetMapping("/seller-application/list")
    public ResponseEntity<PageResponseDTO<SellerResponseDTO>> getPendingApplications(SellerSearchDTO request) {
        log.info("getPendingApplications request: {}", request);
        return ResponseEntity.ok(adminShopService.getPendingApplications(request));
    }


}
