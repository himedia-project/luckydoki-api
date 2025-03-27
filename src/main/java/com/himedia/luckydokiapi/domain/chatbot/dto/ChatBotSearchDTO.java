package com.himedia.luckydokiapi.domain.chatbot.dto;

import com.himedia.luckydokiapi.dto.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class ChatBotSearchDTO extends PageRequestDTO {

    private String searchKeyword;
}
