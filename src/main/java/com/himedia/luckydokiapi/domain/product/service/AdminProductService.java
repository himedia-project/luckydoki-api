package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.util.NumberGenerator;
import com.himedia.luckydokiapi.util.file.CustomFileUtil;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.himedia.luckydokiapi.util.NumberGenerator.*;

// API 명세서 -> JDOC
public interface AdminProductService {


    PageResponseDTO<ProductDTO> list(ProductRequestDTO requestDTO);

    ProductDTO getOne(Long id);

    Long register(ProductDTO productDTO);

    Long modify(Long id, ProductDTO productDTO);

    void remove(Long id);

    /**
     * Product -> ProductDTO 변환
     *
     * @param product Product
     * @return ProductDTO
     */
    default ProductDTO entityToDTO(Product product) {
List<String> tags = product.getProductTagList().stream().map(ProductTag::getTag)
        .map(Tag::getName).toList();
        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .discountRate(product.getDiscountRate())
                .description(product.getDescription())
                .isNew(product.getIsNew())
                .best(product.getBest())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .tagStrList(tags)
                .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return productDTO;
        }

        List<String> fileNameList = imageList.stream().map(ProductImage::getImageName).toList();

        productDTO.setUploadFileNames(fileNameList);
        productDTO.setCategoryId(product.getCategory().getId());

        return productDTO;
    }

    default Product dtoToEntity(ProductDTO dto, Category category) {

        Product product = Product.builder()
                .id(dto.getId())
                .code(generateRandomNumber(10))
                .category(category)
                .name(dto.getName())
                .price(dto.getPrice())
                .discountPrice(dto.getDiscountPrice())
                .discountRate(dto.getDiscountRate())
                .description(dto.getDescription())
                .isNew(dto.getIsNew())
                .best(dto.getBest())
                .stockNumber(dto.getStockNumber())
                .delFlag(false)
                .build();

        //업로드 처리가 끝난 파일들의 이름 리스트
        List<String> uploadFileNames = dto.getUploadFileNames();

        if (uploadFileNames == null) {
            return product;
        }

        // 이미지 파일 업로드 처리
        uploadFileNames.forEach(product::addImageString);

        return product;
    }


}
