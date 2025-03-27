package com.himedia.luckydokiapi.domain.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotMessage;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatBotMessageResponseDTO {

    private String id;
    private String chatRoomId;
    private String email;        // 메시지 보낸 사람의 이메일 (챗봇인 경우 "chatbot")
    private String message;      // 메시지 내용
    @JsonFormat( pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime timestamp;  // 메시지 시간

    public static ChatBotMessageResponseDTO toDto(ChatbotMessage chatbotMessage) {
        return ChatBotMessageResponseDTO.builder()
                .id(chatbotMessage.getId())
                .chatRoomId(chatbotMessage.getChatRoomId())
                .email(chatbotMessage.getEmail())
                .message(chatbotMessage.getMessage())
                .timestamp(chatbotMessage.getTimestamp())
                .build();
    }
}
