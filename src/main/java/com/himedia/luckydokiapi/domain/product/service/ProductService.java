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

    default Product dtoToEntity(ProductDTO.Request dto, Category category, Shop shop) {
        Product product = Product.builder()
                .code(generateRandomNumber(10))
                .category(category)
                .name(dto.getName())
                .price(dto.getPrice())
                .discountPrice(dto.getDiscountPrice())
                .discountRate((int) ((1 - (double) dto.getDiscountPrice() / dto.getPrice()) * 100))
                .description(dto.getDescription())
                .stockNumber(dto.getStockNumber() == null ? 99 : dto.getStockNumber())
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

    default ProductDTO.Response entityToDTO(Product product, String email) {
        boolean isLiked = product.isLikedByUser(email);
        ProductDTO.Response productDTO = ProductDTO.Response.builder()
                .id(product.getId())
                .code(product.getCode())
                .reviewAverage(product.getReviewAverage())
                .reviewCount(product.getReviewCount())
                .nickName(product.getShop().getMember().getNickName())  // shop name으로 사용
                .email(product.getShop().getMember().getEmail())
                .categoryId(product.getCategory().getId())
                .categoryAllName(product.getCategoryAllName())
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
                .shopImage(product.getShop().getImage())
                .stockNumber(product.getStockNumber())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .tagList(product.getTagList())
                .likes(isLiked)
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
