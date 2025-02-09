package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.domain.product.dto.CategoryDTO;
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
public class AdminCategoriesDTO {

    private Long categoryId;
    private String name;
    private String logo;

    // 등록시 이미지 업로드
    private MultipartFile file;

    public void setFileName(String uploadFileName) {
        this.logo = uploadFileName;
    }
}
