package com.himedia.luckydokiapi.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        private String categoryName;
        private String name;
        private Integer price;
        private Integer discountPrice;
        private Integer discountRate;
        private String description;
        private ProductIsNew isNew;
        private ProductBest best;
        private ProductEvent event;
        private Long shopId;
        private String shopName;
        private String shopImage;
        private Integer stockNumber;
        private String nickName;
        private String email;
        
        @Builder.Default
        private List<String> uploadFileNames = new ArrayList<>();
        
        private List<String> imagePathList;
        private List<String> tagStrList;
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime modifiedAt;
    }
}
