package com.himedia.luckydokiapi.domain.search.document;

import com.himedia.luckydokiapi.domain.community.dto.CommunityResponseDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Document(indexName = "communities")
@Getter
@NoArgsConstructor
@Setting(settingPath = "elasticsearch/settings.json")
@Mapping(mappingPath = "elasticsearch/community-mapping.json")
public class CommunityDocument {

    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private Long shopId;

    @Field(type = FieldType.Keyword)
    private String authorImage;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String nickName;

    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String content;

    @Field(type = FieldType.Text)
    private List<String> uploadFileNames;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<CommunityProductDocument> products;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String tags;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private String createdAt;
    
    @Builder
    public CommunityDocument(CommunityResponseDTO dto) {
        this.id = String.valueOf(dto.getId());
        this.shopId = dto.getShopId();
        this.authorImage = dto.getAuthorImage();
        this.nickName = dto.getNickName();
        this.content = dto.getContent();
        this.uploadFileNames = dto.getUploadFileNames();
        this.products = dto.getProductDTOs().stream()
                .map(CommunityProductDocument::new)
                .toList();
        // TagDTO 리스트에서 태그 이름만 추출하여 공백으로 구분된 문자열로 변환
        if (dto.getTagList() != null && !dto.getTagList().isEmpty()) {
            this.tags = dto.getTagList().stream()
                    .map(TagDTO::getName)
                    .collect(Collectors.joining(" "));
        } else {
            this.tags = "";
        }

        // LocalDateTime을 String으로 변환하여 저장
        if (dto.getCreatedAt() != null) {
            this.createdAt = dto.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
} 