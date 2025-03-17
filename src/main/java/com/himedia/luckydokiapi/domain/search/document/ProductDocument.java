package com.himedia.luckydokiapi.domain.search.document;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.enums.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Document(indexName = "products")
@Getter
@NoArgsConstructor
@Setting(settingPath = "elasticsearch/settings.json")
@Mapping(mappingPath = "elasticsearch/product-mapping.json")
public class ProductDocument {
    @Field(type = FieldType.Keyword)
    private String id;
    
    @Field(type = FieldType.Keyword)
    private String code;
    
    @Field(type = FieldType.Keyword)
    private Long categoryId;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String categoryAllName;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String categoryName;
    
    @Field(type = FieldType.Double)
    private double reviewAverage;
    
    @Field(type = FieldType.Integer)
    private int reviewCount;
    
    @Field(type = FieldType.Integer)
    private int likesCount;
    
    @Field(type = FieldType.Integer)
    private int salesCount;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String name;
    
    @Field(type = FieldType.Integer)
    private Integer price;
    
    @Field(type = FieldType.Integer)
    private Integer discountPrice;
    
    @Field(type = FieldType.Integer)
    private Integer discountRate;
    
    @Field(type = FieldType.Keyword)
    private ProductIsNew isNew;
    
    @Field(type = FieldType.Keyword)
    private ProductBest best;
    
    @Field(type = FieldType.Keyword)
    private ProductEvent event;
    
    @Field(type = FieldType.Keyword)
    private ProductApproval approvalStatus;

    
    @Field(type = FieldType.Integer)
    private Integer stockNumber;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String nickName;
    
    @Field(type = FieldType.Text)
    private String email;
    
    @Field(type = FieldType.Text)
    private List<String> uploadFileNames;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String tags;

    
    @Field(type = FieldType.Boolean)
    private Boolean likes;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String modifiedAt;
    
    @Builder
    public ProductDocument(ProductDTO.Response productDTO) {
        this.id = String.valueOf(productDTO.getId());
        this.code = productDTO.getCode();
        this.categoryId = productDTO.getCategoryId();
        this.categoryAllName = productDTO.getCategoryAllName();
        this.categoryName = productDTO.getCategoryName();
        this.reviewAverage = productDTO.getReviewAverage();
        this.reviewCount = productDTO.getReviewCount();
        this.likesCount = productDTO.getLikesCount();
        this.salesCount = productDTO.getSalesCount();
        this.name = productDTO.getName();
        this.price = productDTO.getPrice();
        this.discountPrice = productDTO.getDiscountPrice();
        this.discountRate = productDTO.getDiscountRate();
        this.isNew = productDTO.getIsNew();
        this.best = productDTO.getBest();
        this.event = productDTO.getEvent();
        this.approvalStatus = productDTO.getApprovalStatus();
        this.stockNumber = productDTO.getStockNumber();
        this.nickName = productDTO.getNickName();
        this.email = productDTO.getEmail();
        this.uploadFileNames = productDTO.getUploadFileNames();
        this.likes = productDTO.getLikes();
        
        // TagDTO 리스트에서 태그 이름만 추출하여 공백으로 구분된 문자열로 변환
        if (productDTO.getTagList() != null && !productDTO.getTagList().isEmpty()) {
            this.tags = productDTO.getTagList().stream()
                    .map(TagDTO::getName)
                    .collect(Collectors.joining(" "));
        } else {
            this.tags = "";
        }

        // LocalDateTime을 String으로 변환하여 저장
        if (productDTO.getCreatedAt() != null) {
            this.createdAt = productDTO.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (productDTO.getModifiedAt() != null) {
            this.modifiedAt = productDTO.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
} 