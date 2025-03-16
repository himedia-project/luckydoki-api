package com.himedia.luckydokiapi.domain.search.controller;


import com.himedia.luckydokiapi.domain.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchKeywordService searchKeywordService;


    // 인기 검색어 조회 API
    @GetMapping("/popular-keywords")
    public ResponseEntity<List<String>> getPopularKeywords(
            @RequestParam(defaultValue = "10") int limit) {
        List<String> popularKeywords = searchKeywordService.getPopularKeywords(limit);
        return ResponseEntity.ok(popularKeywords);
    }

    // 인기 검색어와 검색 횟수 조회 API
    @GetMapping("/popular-keywords/with-count")
    public ResponseEntity<List<SearchKeywordService.KeywordCount>> getPopularKeywordsWithCount(
            @RequestParam(defaultValue = "10") int limit) {
        List<SearchKeywordService.KeywordCount> popularKeywords =
                searchKeywordService.getPopularKeywordsWithCount(limit);
        return ResponseEntity.ok(popularKeywords);
    }
}