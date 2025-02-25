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

    void approveProduct(Long id);


    List<ProductDTO.Response> getProductsByApprovalStatus(ProductApproval status);

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


    void modifyProductBest(List<Long> modifyProductIdList);

    void modifyProductIsNew(List<Long> modifyProductIdList);

}
