package com.himedia.luckydokiapi.domain.chat.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String member;
    private Long shopId;
    private String shopImage;
    private String shopName;
    private String lastMessage;

    private LocalDateTime lastMessageTime; // 처음 room 이 만들어진 시간
}
//처음 채팅방이 생성될때 서버에 전달되는 dto