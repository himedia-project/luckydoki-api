package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;

import java.util.List;
import java.util.stream.Collectors;

public interface ProductService {


    ProductResponseDTO getProduct(Long id);

    Product getEntity(Long productId);

    List<ProductResponseDTO> list(ProductRequestDTO requestDTO);

    List<TagDTO> tagList(Long id);


    default ProductResponseDTO entityToDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .description(product.getDescription())
                .mdPick(product.getIsNew())
                .uploadFileNames(product.getImageList().stream().map(ProductImage::getImageName).collect(Collectors.toList()))
                .imagePathList(product.getImageList().stream().map(ProductImage::getImageName).collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }



}
