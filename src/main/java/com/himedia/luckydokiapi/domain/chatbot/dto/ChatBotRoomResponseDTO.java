package com.himedia.luckydokiapi.domain.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotRoom;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatBotRoomResponseDTO {

    private String id;
    private String userEmail;    // null 또는 빈 문자열 가능
    private String sessionId;    // 비로그인 사용자 식별용
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastResponseTime;
    private String lastMessage;
    private boolean active;  // 채팅방 활성화 상태


    public static ChatBotRoomResponseDTO toDto(ChatbotRoom chatbotRoom) {
        return ChatBotRoomResponseDTO.builder()
                .id(chatbotRoom.getId())
                .userEmail(chatbotRoom.getUserEmail())
                .sessionId(chatbotRoom.getSessionId())
                .startTime(chatbotRoom.getStartTime())
                .lastResponseTime(chatbotRoom.getLastResponseTime())
                .lastMessage(chatbotRoom.getLastMessage())
                .active(chatbotRoom.isActive())
                .build();
    }
}
