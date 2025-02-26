package com.himedia.luckydokiapi.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "셀러 승급 신청 dto")
public class SellerRequestDTO {


    private MultipartFile profileImage;

    @NotBlank
    @Size(min = 2, max = 100, message = "소개글은 100자 이하로 입력해주세요.")
    private String introduction;
}
