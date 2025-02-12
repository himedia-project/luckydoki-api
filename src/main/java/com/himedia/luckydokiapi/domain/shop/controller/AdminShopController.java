package com.himedia.luckydokiapi.domain.shop.controller;

import com.himedia.luckydokiapi.domain.member.dto.SellerResponseDTO;
import com.himedia.luckydokiapi.domain.shop.service.AdminShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/admin/shop")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    /**
     *  셀러 신청 승인 API
     */
    @PostMapping("/approve-seller/{applicationId}")
    public ResponseEntity<?> approve(@PathVariable Long applicationId) {
        log.info("approve applicationId: {}", applicationId);
        return ResponseEntity.ok(adminShopService.approveSeller(applicationId));
    }

    /**
     *  승인되지 않은 셀러 신청 목록 조회
     */
    @GetMapping("/seller-applications/pending")
    public ResponseEntity<List<SellerResponseDTO>> getPendingApplications() {
        return ResponseEntity.ok(adminShopService.getPendingApplications());
    }

    /**
     *  승인된 셀러 신청 목록 조회
     */
    @GetMapping("/seller-applications/approved")
    public ResponseEntity<List<SellerResponseDTO>> getApprovedApplications() {
        return ResponseEntity.ok(adminShopService.getApprovedApplications());
    }

}
