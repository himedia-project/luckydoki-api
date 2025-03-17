package com.himedia.luckydokiapi.domain.search.controller;

import com.himedia.luckydokiapi.domain.search.service.SearchIndexBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/search/index")
@RequiredArgsConstructor
@Tag(name = "search-index", description = "검색 인덱스 관리 API")
public class SearchIndexController {
    private final SearchIndexBatchService searchIndexBatchService;

    @PostMapping("/recreate")
    @Operation(summary = "검색 인덱스 재생성", description = "전체 검색 인덱스를 재생성합니다. (관리자 전용)")
    public ResponseEntity<String> recreateIndices() {
        try {
            searchIndexBatchService.recreateIndices();
            return ResponseEntity.ok("Successfully recreated indices");
        } catch (Exception e) {
            log.error("Error recreating indices", e);
            return ResponseEntity.internalServerError().body("Error recreating indices: " + e.getMessage());
        }
    }

    @PostMapping("/reindex")
    @Operation(summary = "전체 데이터 재인덱싱", description = "모든 상품과 커뮤니티 데이터를 재인덱싱합니다. (관리자 전용)")
    public ResponseEntity<String> reindexAll() {
        try {
            searchIndexBatchService.reindexAllData();
            return ResponseEntity.ok("Successfully reindexed all data");
        } catch (Exception e) {
            log.error("Error reindexing data", e);
            return ResponseEntity.internalServerError().body("Error reindexing data: " + e.getMessage());
        }
    }
} 