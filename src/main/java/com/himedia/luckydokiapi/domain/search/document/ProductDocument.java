package com.himedia.luckydokiapi.domain.search.document;

import com.himedia.luckydokiapi.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "products")
@Getter
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
    public ProductDocument(Product product) {
        this.id = String.valueOf(product.getId());
        this.name = product.getName();
        this.categoryName = product.getCategory().getName();
        this.tags = product.getProductTagList().stream()
                .map(pt -> pt.getTag().getName())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
} 