package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.util.List;
import java.time.LocalDateTime;

import static com.himedia.luckydokiapi.util.NumberGenerator.generateRandomNumber;

public interface ProductService {


    ProductDTO.Response getProduct(Long id, String email);

    Product getEntity(Long productId);

    List<ProductDTO.Response> list(ProductSearchDTO requestDTO, String email);

    List<TagDTO> tagList(Long id);

    List<ProductDTO.Response> getListByMember(String email);

    Long createProduct(String email, ProductDTO.Request dto);

    Long updateProduct(String email, ProductDTO.Request dto, Long productId);

    void deleteProductById(Long productId);

    List<ProductDTO.Response> recommendList(ProductDTO.Request request, String email);

    /**
     * 상품 수량 유효성 검사
     * @param id 상품 ID
     * @param count 상품 수량
     */
    void validateProductCount(Long id, Integer count);

    /**
     * 특정 시간 이후에 변경된 상품 ID 목록을 조회합니다.
     * @param fromTime 이 시간 이후로 변경된 상품을 조회
     * @return 변경된 상품 ID 목록
     */
    List<Long> getRecentlyChangedProducts(LocalDateTime fromTime);

    /**
     * 특정 시간 이후에 추가된 상품 ID 목록을 조회합니다.
     * @param fromTime 이 시간 이후로 추가된 상품을 조회
     * @return 추가된 상품 ID 목록
     */
    List<Long> getRecentlyAddedProducts(LocalDateTime fromTime);




}
