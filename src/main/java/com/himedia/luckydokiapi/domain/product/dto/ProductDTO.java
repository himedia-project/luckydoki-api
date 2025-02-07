package com.himedia.luckydokiapi.domain.product.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.product.enums.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductDTO {
    //어드민 , member  싱품 등록/수정 용 dto
    //여기엔 member email 없음
    private Long id;
    private String code;
    private Long categoryId;
    private String categoryName;
    private String name;
    private Integer price;
    private Integer discountPrice;
    private Integer discountRate;
    private String description;
    private ProductDisplay display;
    private ProductApproval approval;
    private ProductIsNew isNew;
    private ProductBest best;
    private ProductEvent event;
    private Long shopId;

    private Integer stockNumber;
//    private Integer delFlag;
//    private List


    // 파일 입력값
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    // 파일 업로드한 url 응답값
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();

    // excel 등록시 image path list
    private List<String> imagePathList;

    // 상품 태그 문자열 리스트 "액션,스포츠,연애"
    private List<String> tagStrList;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;


}
