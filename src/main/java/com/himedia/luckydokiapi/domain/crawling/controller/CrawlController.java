package com.himedia.luckydokiapi.domain.crawling.controller;

import com.himedia.luckydokiapi.domain.crawling.dto.CrawlRequestDTO;
import com.himedia.luckydokiapi.domain.crawling.service.CrawlService;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlController {

    private final CrawlService crawlService;
    private final AdminProductService productService;

    @GetMapping("/url")
    public Map<String, Object> test(CrawlRequestDTO requestDTO) {
        List<ProductDTO.Request> crawled = crawlService.crawl(requestDTO.getUrl());
//        productService.registerAll(crawled);

        return Map.of("result", crawled);
    }
}
