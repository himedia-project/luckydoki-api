package com.himedia.luckydokiapi.domain.product.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CategoryDTO {

    private Long categoryId;
    private String name;
    private String logo;

    // 등록시 이미지 업로드
    private MultipartFile file;

    public void setFileName(String uploadFileName) {
        this.logo = uploadFileName;
    }
}
