package com.himedia.luckydokiapi.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;
import com.himedia.luckydokiapi.domain.product.enums.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class ProductDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Request {

        private Long id;
        //해당 최하위 카테고리 (헷깔리지 않기)
        //여성/잡화/신발/아디다스 에서 '아디다스' 의 카테고리 번호임
        private Long categoryId;
        private String name;
        private Integer price;
        private Integer discountPrice;
        private String description;
        private Long shopId;
        private Integer stockNumber;

        @Builder.Default
        private List<MultipartFile> files = new ArrayList<>();

        @Builder.Default
        private List<String> uploadFileNames = new ArrayList<>();

        // 상품 엑셀 업로드시 사용
        private List<String> imagePathList;
        private List<String> tagStrList;
        private List<String> reviewList;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Response {
        private Long id;
        private String code;
        private Long categoryId;
        private String categoryAllName;
        private String categoryName;
        private double reviewAverage;
        private int reviewCount;
        private int likesCount;
        // 판매량
        private int salesCount;
        private String name;
        private Integer price;
        private Integer discountPrice;
        private Integer discountRate;
        private String description;
        private ProductIsNew isNew;
        private ProductBest best;
        private ProductEvent event;
        private ProductApproval approvalStatus;
        private Long shopId;
        private String shopName;
        private String shopImage;
        private Integer stockNumber;
        private String nickName;
        private String email;

        @Builder.Default
        private List<String> uploadFileNames = new ArrayList<>();

        private List<TagDTO> tagList;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime modifiedAt;

        private Boolean likes;

        public static Response toDto(Product product) {
            ProductDTO.Response productDTO = Response.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .nickName(product.getShop().getMember().getNickName())  // shop name으로 사용
                    .email(product.getShop().getMember().getEmail())
                    .categoryId(product.getCategory().getId())
                    .categoryAllName(product.getCategoryAllName())
                    .categoryName(product.getCategory().getName())
                    .reviewAverage(product.getReviewAverage())
                    .reviewCount(product.getReviewCount())
                    .likesCount(product.getLikesCount())
                    .salesCount(product.getSalesCount())
                    .name(product.getName())
                    .price(product.getPrice())
                    .discountPrice(product.getDiscountPrice())
                    .discountRate(product.getDiscountRate())
                    .description(product.getDescription())
                    .isNew(product.getIsNew())
                    .best(product.getBest())
                    .event(product.getEvent())
                    .approvalStatus(product.getApprovalStatus())
                    .shopId(product.getShop().getId())
                    .shopName(product.getShop().getMember().getNickName())
                    .shopImage(product.getShop().getMember().getProfileImage())
                    .stockNumber(product.getStockNumber())
                    .tagList(product.getTagList())
                    .createdAt(product.getCreatedAt())
                    .modifiedAt(product.getModifiedAt())
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

        public static Response toDto(Product product, String email) {
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

        public static ProductDTO.Request entityToReqDTO(Product product) {
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

    }
}
