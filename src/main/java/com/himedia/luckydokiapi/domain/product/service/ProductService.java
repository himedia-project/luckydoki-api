package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductRequestDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductResponseDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.util.List;

import static com.himedia.luckydokiapi.util.NumberGenerator.generateRandomNumber;

public interface ProductService {


    ProductResponseDTO getProduct(Long id);

    Product getEntity(Long productId);

    List<ProductResponseDTO> list(ProductRequestDTO requestDTO);

    List<TagDTO> tagList(Long id);

    List<ProductResponseDTO> getListByMember(String email);

    Long createProduct(String email, ProductDTO dto);

    Long updateProduct(String email, ProductDTO dto, Long productId);

    void deleteProductById(Long productId);


    default Product dtoToEntity(ProductDTO dto, Category category, Shop shop) {
        Product product = Product.builder()
                .code(generateRandomNumber(10))
                .category(category)
                .name(dto.getName())
                .price(dto.getPrice())
                .discountPrice(dto.getDiscountPrice())
                .discountRate((int) ((1 - (double) dto.getDiscountPrice() / dto.getPrice()) * 100))
                .description(dto.getDescription())
                .stockNumber(dto.getStockNumber())
                .shop(shop)
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


    default ProductDTO entityToDTO(Product product) {
        List<String> tags = product.getProductTagList().stream().map(ProductTag::getTag)
                .map(Tag::getName).toList();
        ProductDTO productDTO = ProductDTO.builder()
                .nickName(product.getShop().getMember().getNickName())
                .email(product.getShop().getMember().getEmail())
                .id(product.getId())
                .code(product.getCode())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .description(product.getDescription())
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

    default ProductResponseDTO entityToResDTO(Product product) {
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
}
