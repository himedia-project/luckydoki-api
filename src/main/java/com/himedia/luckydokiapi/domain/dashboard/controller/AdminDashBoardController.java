package com.himedia.luckydokiapi.domain.dashboard.controller;

import com.himedia.luckydokiapi.domain.dashboard.dto.DashboardDTO;
import com.himedia.luckydokiapi.domain.dashboard.service.AdminDashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashBoardController {

    private final AdminDashBoardService dashBoardService;

    @GetMapping
    public ResponseEntity<?> getDashBoardData() {
        log.info("getDashBoardData");
        DashboardDTO dashBoard = dashBoardService.getDashBoard();
        return ResponseEntity.ok(dashBoard);
    }


}
