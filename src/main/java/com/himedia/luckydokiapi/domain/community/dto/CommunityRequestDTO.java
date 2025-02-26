package com.himedia.luckydokiapi.domain.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커뮤니티 글쓰기 요청 dto")
public class CommunityRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    // 업로드용 파일 필드
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();

    @Builder.Default
    private List<Long> productIds = new ArrayList<>();

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    private List<String> tagStrList;

}
