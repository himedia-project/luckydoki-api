package com.himedia.luckydokiapi.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String sender; //먼저 보낸사람
    private Long shopId;
    private String shopImage;
    private String shopName;
    private String lastMessage;
    private LocalDateTime createdAt;

    //    @JsonFormat( pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastMessageTime;
}
//처음 채팅방이 생성될때 서버에 전달되는 dto