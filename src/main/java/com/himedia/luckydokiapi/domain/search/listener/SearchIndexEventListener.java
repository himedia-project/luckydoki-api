package com.himedia.luckydokiapi.domain.search.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.search.document.CommunityDocument;
import com.himedia.luckydokiapi.domain.search.document.ProductDocument;
import com.himedia.luckydokiapi.domain.search.event.CommunityIndexEvent;
import com.himedia.luckydokiapi.domain.search.event.ProductIndexEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class SearchIndexEventListener {
    private final ElasticsearchClient elasticsearchClient;
    private final ProductRepository productRepository;
    private final CommunityRepository communityRepository;

    @Async
    @EventListener
    public void handleProductIndexEvent(ProductIndexEvent event) {
        try {
            switch (event.getOperation()) {
                case "DELETE" -> {
                    DeleteRequest request = DeleteRequest.of(r -> r
                            .index("products")
                            .id(event.getId().toString())
                    );
                    elasticsearchClient.delete(request);
                }
                case "CREATE", "UPDATE" -> {
                    Product product = productRepository.findById(event.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                    
                    // Product 객체를 DTO로 변환
                    ProductDTO.Response productDTO = ProductDTO.Response.from(product);
                    
                    // DTO를 사용하여 ProductDocument 생성
                    ProductDocument document = new ProductDocument(productDTO);
                    
                    IndexRequest<ProductDocument> request = IndexRequest.of(r -> r
                            .index("products")
                            .id(event.getId().toString())
                            .document(document)
                    );
                    elasticsearchClient.index(request);
                }
            }
        } catch (Exception e) {
            log.error("Error handling product index event", e);
        }
    }

    @Async
    @EventListener
    public void handleCommunityIndexEvent(CommunityIndexEvent event) {
        try {
            switch (event.getOperation()) {
                case "DELETE" -> {
                    DeleteRequest request = DeleteRequest.of(r -> r
                            .index("communities")
                            .id(event.getId().toString())
                    );
                    elasticsearchClient.delete(request);
                }
                case "CREATE", "UPDATE" -> {
                    Community community = communityRepository.findById(event.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Community not found"));
                    CommunityDocument document = new CommunityDocument(community);
                    IndexRequest<CommunityDocument> request = IndexRequest.of(r -> r
                            .index("communities")
                            .id(event.getId().toString())
                            .document(document)
                    );
                    elasticsearchClient.index(request);
                }
            }
        } catch (Exception e) {
            log.error("Error handling community index event", e);
        }
    }
} 