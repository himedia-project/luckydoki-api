package com.himedia.luckydokiapi.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 정보 수정을 위한 dto ")
public class UpdateMemberDTO {
    private String nickName;
    private String phone;

    private MultipartFile file; // 업로드할 파일
}
