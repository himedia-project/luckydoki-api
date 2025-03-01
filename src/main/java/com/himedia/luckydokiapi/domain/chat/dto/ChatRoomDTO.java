package com.himedia.luckydokiapi.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 생성 요청/응답 DTO")
public class ChatRoomDTO {
    @Schema(description = "채팅방을 식별하는 id ")
    private Long id;
    @Schema(description = "request 시엔 null , response 일 경우엔 채팅 상대방의 이메일이 들어간다 ")
    private String sender;
    private Long shopId;
    private String shopImage;
    private String memberImage;
    private String shopName;
    private String lastMessage;
    private LocalDateTime createdAt;

    //    @JsonFormat( pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastMessageTime;
}
//처음 채팅방이 생성될때 서버에 전달되는 dto