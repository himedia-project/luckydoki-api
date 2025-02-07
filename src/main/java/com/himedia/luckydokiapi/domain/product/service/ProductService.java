package com.himedia.luckydokiapi.domain.product.service;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.ProductSearchDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.entity.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.util.List;

import static com.himedia.luckydokiapi.util.NumberGenerator.generateRandomNumber;

public interface ProductService {


    ProductDTO.Response getProduct(Long id);

    Product getEntity(Long productId);

    List<ProductDTO.Response> list(ProductSearchDTO requestDTO);

    List<TagDTO> tagList(Long id);

    List<ProductDTO.Response> getListByMember(String email);

    Long createProduct(String email, ProductDTO.Request dto);

    Long updateProduct(String email, ProductDTO.Request dto, Long productId);

    void deleteProductById(Long productId);


    default Product dtoToEntity(ProductDTO.Request dto, Category category, Shop shop) {
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


    default ProductDTO.Response entityToDTO(Product product) {
        List<String> tags = product.getProductTagList().stream().map(ProductTag::getTag)
                .map(Tag::getName).toList();
        ProductDTO.Response productDTO = ProductDTO.Response.builder()
                .id(product.getId())
                .code(product.getCode())
                .nickName(product.getShop().getMember().getNickName())  // shop name으로 사용
                .email(product.getShop().getMember().getEmail())
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
                .shopId(product.getShop().getId())
                .shopName(product.getShop().getMember().getNickName())
                .shopImage(product.getShop().getMember().getProfileImage())
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

}
