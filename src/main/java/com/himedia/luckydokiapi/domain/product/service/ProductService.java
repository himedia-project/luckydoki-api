package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;
import com.himedia.luckydokiapi.domain.product.entity.ProductTag;
import com.himedia.luckydokiapi.domain.product.entity.Tag;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import com.himedia.luckydokiapi.security.MemberDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface ProductService {


    ProductResponseDTO getProduct(Long id);

    Product getEntity(Long productId);

    List<ProductResponseDTO> list(ProductRequestDTO requestDTO);

    List<TagDTO> tagList(Long id);

    default ProductResponseDTO entityToDTO(Product product) {
        List<String> tags = product.getProductTagList().stream().map(ProductTag::getTag)
                .map(Tag::getName).toList();
        ProductResponseDTO productDTO = ProductResponseDTO.builder()
                .nickName(product.getShop().getMember().getNickName())
                .email(product.getShop().getMember().getEmail())
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

    List<ProductResponseDTO> getListByMember(String email);

    void createProduct(String email, ProductDTO dto);

    void updateProduct(String email, ProductDTO dto, Long productId);

    void deleteProductById(Long productId);
}
