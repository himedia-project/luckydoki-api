package com.himedia.luckydokiapi.domain.chatbot.service;


import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotMessageResponseDTO;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotRoomResponseDTO;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotSearchDTO;
import com.himedia.luckydokiapi.dto.PageResponseDTO;

import java.util.List;

public interface ChatBotService {
    PageResponseDTO<ChatBotRoomResponseDTO> getRoomList(ChatBotSearchDTO request);

    List<ChatBotMessageResponseDTO> getChatBotMessageList(String roomId);
}
