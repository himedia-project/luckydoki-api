package com.himedia.luckydokiapi.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "채팅 상세 내역 조회")
public class ChatHistoryDTO {
    @Schema(description = "채팅방을 식별하는 id , ChatRoomDTO 에 있는 id를 참조한다 ")
    private Long roomId;
    @Schema(description = "메세지를 보낸 사용자의 이메일")
    private String email;
    private Long shopId;
    private String shopImage;
    private String message;     // 채팅 내용
    @JsonFormat( pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastMessageTime; //마지막 대화 시간
}
