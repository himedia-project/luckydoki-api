package com.himedia.luckydokiapi.domain.product.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.product.enums.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductResponseDTO {
    //응답용 dto
    //member email, nickname 같이 있음
    private String nickName;
    private String email;
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
    private List<String> tagStrList;
    private Integer stockNumber;

    private List<String> imagePathList;

    // 파일 업로드한 url 응답값
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
}


