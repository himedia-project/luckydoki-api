package com.himedia.luckydokiapi.domain.search.document;


import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class CommunityProductDocument {
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer price;

    @Field(type = FieldType.Integer)
    private Integer discountPrice;

    @Field(type = FieldType.Text)
    private List<String> uploadFileNames;

    @Builder
    public CommunityProductDocument(ProductDTO.Response productDTO) {
        this.id = String.valueOf(productDTO.getId());
        this.name = productDTO.getName();
        this.price = productDTO.getPrice();
        this.discountPrice = productDTO.getDiscountPrice();
        this.uploadFileNames = productDTO.getUploadFileNames();
    }
}