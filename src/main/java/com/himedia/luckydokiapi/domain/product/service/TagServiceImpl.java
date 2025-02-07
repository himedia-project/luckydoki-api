package com.himedia.luckydokiapi.domain.product.service;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.product.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final AdminProductService adminProductService;

    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO.Response> list(Long id) {
        List<Long> productIds = tagRepository.findByTag(id);
        if (productIds == null || productIds.isEmpty()) {
            throw new EntityNotFoundException("해당 엔티티가 없습니다.");
        }

        return productIds.stream().map(productId -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 엔티티가 없습니다."));
            return adminProductService.entityToDTO(product);
        }).toList();
    }
}
