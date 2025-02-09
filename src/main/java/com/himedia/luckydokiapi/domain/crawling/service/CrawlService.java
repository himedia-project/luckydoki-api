package com.himedia.luckydokiapi.domain.crawling.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;

import java.util.List;

public interface CrawlService {

    List<ProductDTO.Request> crawl(String url);
}
