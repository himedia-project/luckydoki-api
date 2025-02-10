package com.himedia.luckydokiapi.domain.product.dto;

import com.himedia.luckydokiapi.domain.product.entity.Category;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CategoryDTO {
    private Long categoryId;
    private String name;
    private Set<CategoryDTO> children = new HashSet<>();
    private String logo;

    // 등록시 이미지 업로드
    private MultipartFile file;

    public void setFileName(String uploadFileName) {
        this.logo = uploadFileName;
    }
}
