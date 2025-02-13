package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.dto.PageResponseDTO;


import java.util.List;


// API 명세서 -> JDOC
public interface AdminProductService {


    PageResponseDTO<ProductDTO.Response> list(ProductSearchDTO requestDTO);

    ProductDTO.Response getOne(Long id);

    Long register(ProductDTO.Request productDTO);

    Long modify(Long id, ProductDTO.Request productDTO);

    void remove(Long id);


    default ProductDTO.Request entityToReqDTO(Product product) {
        ProductDTO.Request request = ProductDTO.Request.builder()
                .categoryId(product.getCategory().getId())
                .name(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .description(product.getDescription())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return request;
        }

        List<String> fileNameList = imageList.stream().map(ProductImage::getImageName).toList();

        request.setUploadFileNames(fileNameList);
        request.setCategoryId(product.getCategory().getId());

        return request;
    }


    /**
     * Product -> ProductDTO 변환
     *
     * @param product Product
     * @return ProductDTO
     */
    default ProductDTO.Response entityToDTO(Product product) {
        List<String> tags = product.getProductTagList().stream().map(ProductTag::getTag)
                .map(Tag::getName).toList();
        ProductDTO.Response productDTO = ProductDTO.Response.builder()
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
                .event(product.getEvent())
                .email(product.getShop().getMember().getEmail())
                .shopId(product.getShop().getId())
                .shopName(product.getShop().getMember().getNickName())
                .stockNumber(product.getStockNumber())
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

    void modifyProductBest(List<Long> modifyProductIdList);

    void modifyProductIsNew(List<Long> modifyProductIdList);

}
