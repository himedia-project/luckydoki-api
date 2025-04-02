package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.dto.PageResponseDTO;


import java.util.List;


// API 명세서 -> JDOC
public interface AdminProductService {


    PageResponseDTO<ProductDTO.Response> list(ProductSearchDTO requestDTO);

    ProductDTO.Response getOne(Long id);

    Long register(ProductDTO.Request productDTO);

    Long modify(Long id, ProductDTO.Request productDTO);

    void remove(Long id);

    void removeAll(List<Long> productIds);

    void approveProduct(List<Long> productIdList);

    void approveProductAll(List<Long> productIds);

    List<ProductDTO.Response> getProductsByApprovalStatus(ProductApproval status);

    void modifyProductBest(List<Long> modifyProductIdList);

    void modifyProductIsNew(List<Long> modifyProductIdList);

    Long copyProduct(Long productId);
}
