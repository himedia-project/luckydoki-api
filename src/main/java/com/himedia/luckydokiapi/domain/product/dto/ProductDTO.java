package com.himedia.luckydokiapi.domain.product.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductMdPick;
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
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private Integer price;
    private Integer discountPrice;
    private String description;
    private ProductMdPick mdPick;
    private ProductBest best;


    private Integer stockNumber;
//    private Integer delFlag;
//    private List

    private String searchKeyword;

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
