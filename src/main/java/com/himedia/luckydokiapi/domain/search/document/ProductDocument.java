package com.himedia.luckydokiapi.domain.search.document;

import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.annotations.*;

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
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String categoryName;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String tags;
    
    @Builder
    public ProductDocument(ProductDTO.Response productDTO) {
        this.id = String.valueOf(productDTO.getId());
        this.name = productDTO.getName();
        this.categoryName = productDTO.getCategoryName();
        
        // TagDTO 리스트에서 태그 이름만 추출하여 공백으로 구분된 문자열로 변환
        if (productDTO.getTagList() != null && !productDTO.getTagList().isEmpty()) {
            this.tags = productDTO.getTagList().stream()
                    .map(TagDTO::getName)
                    .collect(Collectors.joining(" "));
        } else {
            this.tags = "";
        }
    }

} 