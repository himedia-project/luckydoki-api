package com.himedia.luckydokiapi.domain.event.dto;

import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchDto extends PageRequestDTO {

    private String searchKeyword;
}
