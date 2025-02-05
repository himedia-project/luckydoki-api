package com.himedia.luckydokiapi.domain.member.dto;

import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class MemberRequestDTO extends PageRequestDTO {

    private String email;
    private String nickName;
    private String phone;
    private String birthday;

    private String searchKeyword;
}
