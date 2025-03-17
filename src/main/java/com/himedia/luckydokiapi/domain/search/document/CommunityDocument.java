package com.himedia.luckydokiapi.domain.search.document;

import com.himedia.luckydokiapi.domain.community.entity.Community;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "communities")
@Getter
@Setting(settingPath = "elasticsearch/settings.json")
@Mapping(mappingPath = "elasticsearch/community-mapping.json")
public class CommunityDocument {
    @Field(type = FieldType.Keyword)
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String content;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String productNames;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String tags;
    
    @Builder
    public CommunityDocument(Community community) {
        this.id = String.valueOf(community.getId());
        this.content = community.getContent();
        this.productNames = community.getCommunityProductList().stream()
                .map(cp -> cp.getProduct().getName())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
        this.tags = community.getCommunityTagList().stream()
                .map(ct -> ct.getTag().getName())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
} 