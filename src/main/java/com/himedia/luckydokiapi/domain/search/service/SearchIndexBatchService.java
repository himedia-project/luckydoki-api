package com.himedia.luckydokiapi.domain.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.community.dto.CommunitySearchDTO;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.community.repository.CommunityRepository;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.search.document.CommunityDocument;
import com.himedia.luckydokiapi.domain.search.document.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchIndexBatchService {
    private final ElasticsearchClient elasticsearchClient;
    private final ProductRepository productRepository;
    private final CommunityRepository communityRepository;

    public void recreateIndices() throws IOException {
        recreateProductIndex();
        recreateCommunityIndex();
    }

    public void recreateProductIndex() throws IOException {
        String indexName = "products";
        
        // 기존 인덱스 삭제
        try {
            DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(r -> r.index(indexName));
            elasticsearchClient.indices().delete(deleteRequest);
        } catch (Exception e) {
            log.warn("Error deleting product index", e);
        }

        // 인덱스 생성 및 설정
        String settings = loadResourceFile("elasticsearch/settings.json");
        String mapping = loadResourceFile("elasticsearch/product-mapping.json");
        
        // String을 InputStream으로 변환
        try (InputStream settingsStream = new ByteArrayInputStream(settings.getBytes(StandardCharsets.UTF_8))) {
            elasticsearchClient.indices().create(c -> c
                    .index(indexName)
                    .settings(s -> s.withJson(settingsStream))
            );
        }

        // String을 InputStream으로 변환
        try (InputStream mappingStream = new ByteArrayInputStream(mapping.getBytes(StandardCharsets.UTF_8))) {
            PutMappingRequest putMappingRequest = PutMappingRequest.of(r -> r
                    .index(indexName)
                    .withJson(mappingStream)
            );
            elasticsearchClient.indices().putMapping(putMappingRequest);
        }
    }

    public void recreateCommunityIndex() throws IOException {
        String indexName = "communities";
        
        // 기존 인덱스 삭제
        try {
            DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(r -> r.index(indexName));
            elasticsearchClient.indices().delete(deleteRequest);
        } catch (Exception e) {
            log.warn("Error deleting community index", e);
        }

        // 인덱스 생성 및 설정
        String settings = loadResourceFile("elasticsearch/settings.json");
        String mapping = loadResourceFile("elasticsearch/community-mapping.json");
        
        // String을 InputStream으로 변환
        try (InputStream settingsStream = new ByteArrayInputStream(settings.getBytes(StandardCharsets.UTF_8))) {
            elasticsearchClient.indices().create(c -> c
                    .index(indexName)
                    .settings(s -> s.withJson(settingsStream))
            );
        }

        // String을 InputStream으로 변환
        try (InputStream mappingStream = new ByteArrayInputStream(mapping.getBytes(StandardCharsets.UTF_8))) {
            PutMappingRequest putMappingRequest = PutMappingRequest.of(r -> r
                    .index(indexName)
                    .withJson(mappingStream)
            );
            elasticsearchClient.indices().putMapping(putMappingRequest);
        }
    }


    @Transactional(readOnly = true)
    public void reindexAllData() {
        reindexAllProducts();
        reindexAllCommunities();
    }


    public void reindexAllProducts() {
        List<Product> products = productRepository.findByDTO(ProductSearchDTO.builder().build()).stream()
                .filter(product -> product.getApprovalStatus() == ProductApproval.Y)
                .toList();
        
        for (Product product : products) {
            try {
                // Product 객체를 DTO로 변환
                ProductDTO.Response productDTO = ProductDTO.Response.toDto(product);
                
                // DTO를 사용하여 ProductDocument 생성
                ProductDocument document = new ProductDocument(productDTO);
                
                elasticsearchClient.index(i -> i
                        .index("products")
                        .id(product.getId().toString())
                        .document(document)
                );
            } catch (Exception e) {
                log.error("Error indexing product {}", product.getId(), e);
            }
        }
    }


    public void reindexAllCommunities() {
        List<Community> communities = communityRepository.findByDTO(CommunitySearchDTO.builder().build());
        for (Community community : communities) {
            try {

                CommunityResponseDTO communityDTO = CommunityResponseDTO.toDto(community);

                CommunityDocument document = new CommunityDocument(communityDTO);
                elasticsearchClient.index(i -> i
                        .index("communities")
                        .id(community.getId().toString())
                        .document(document)
                );
            } catch (Exception e) {
                log.error("Error indexing community {}", community.getId(), e);
            }
        }
    }

    private String loadResourceFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    // 매일 새벽 2시에 전체 인덱스 재생성
    @Scheduled(cron = "0 0 2 * * ?")
    // TEST 3분 마다 실행
//     @Scheduled(fixedRate = 180000)
    public void scheduledReindexing() {
        try {
            log.info("Starting scheduled reindexing");
            recreateIndices();
            reindexAllData();
            log.info("Completed scheduled reindexing");
        } catch (Exception e) {
            log.error("Error during scheduled reindexing", e);
        }
    }
} 