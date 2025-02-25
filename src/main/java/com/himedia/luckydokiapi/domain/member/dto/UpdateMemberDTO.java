package com.himedia.luckydokiapi.domain.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberDTO {
    private String nickName;
    private String phone;

    private MultipartFile file; // 업로드할 파일
}
