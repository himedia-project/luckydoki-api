package com.himedia.luckydokiapi.domain.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.himedia.luckydokiapi.domain.search.document.CommunityDocument;
import com.himedia.luckydokiapi.domain.search.document.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchClient elasticsearchClient;

    public List<ProductDocument> searchProducts(String keyword) throws IOException {
        SearchRequest request = SearchRequest.of(r -> r
                .index("products")
                .query(q -> q
                        .multiMatch(m -> m
                                .query(keyword)
                                .fields("name", "categoryName", "tags")
                                .analyzer("korean")
                        )
                )
        );

        SearchResponse<ProductDocument> response = elasticsearchClient.search(request, ProductDocument.class);
        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }

    public List<CommunityDocument> searchCommunities(String keyword) throws IOException {
        SearchRequest request = SearchRequest.of(r -> r
                .index("communities")
                .query(q -> q
                        .multiMatch(m -> m
                                .query(keyword)
                                .fields("content", "productNames", "tags")
                                .analyzer("korean")
                        )
                )
        );

        SearchResponse<CommunityDocument> response = elasticsearchClient.search(request, CommunityDocument.class);
        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
    }
} 