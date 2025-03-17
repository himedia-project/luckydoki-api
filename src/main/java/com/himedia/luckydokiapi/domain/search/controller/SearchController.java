package com.himedia.luckydokiapi.domain.search.controller;


import com.himedia.luckydokiapi.domain.search.document.CommunityDocument;
import com.himedia.luckydokiapi.domain.search.document.ProductDocument;
import com.himedia.luckydokiapi.domain.search.service.SearchKeywordService;
import com.himedia.luckydokiapi.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchKeywordService searchKeywordService;

    private final SearchService searchService;

    /**
     * elasticsearch를 이용한 상품 검색
     * @param keyword 검색 키워드
     * @return 검색 결과
     * @throws IOException 검색 결과를 가져오는 도중 발생한 예외
     */

    // 상품 검색 API
    @GetMapping("/products")
    public ResponseEntity<List<ProductDocument>> searchProducts(@RequestParam String keyword) throws IOException, IOException {
        return ResponseEntity.ok(searchService.searchProducts(keyword));
    }

    // 커뮤니티 검색 API
    @GetMapping("/communities")
    public ResponseEntity<List<CommunityDocument>> searchCommunities(@RequestParam String keyword) throws IOException {
        return ResponseEntity.ok(searchService.searchCommunities(keyword));
    }

    /**
     * 인기 검색어 조회 rest api
     * @param limit 조회할 인기 검색어 개수
     * @return 인기 검색어 목록
     */

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