package com.himedia.luckydokiapi.domain.chatbot.controller;

import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotMessageResponseDTO;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotRoomResponseDTO;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotSearchDTO;
import com.himedia.luckydokiapi.domain.chatbot.service.ChatBotService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/admin/chatbot")
@RestController
@RequiredArgsConstructor
public class AdminChatBotController {

    private final ChatBotService chatBotService;

    @GetMapping("/room/list")
    public ResponseEntity<PageResponseDTO<ChatBotRoomResponseDTO>> getRoomList(ChatBotSearchDTO request) {
        log.info("getRoomList request: {}", request);
        PageResponseDTO<ChatBotRoomResponseDTO> dto = chatBotService.getRoomList(request);
        return ResponseEntity.ok(dto);
    }

    // 해당 roomId 별 채팅내역 리스트
    @GetMapping("/room/{roomId}/chat/list")
    public ResponseEntity<List<ChatBotMessageResponseDTO>> getChatHistoryList(@PathVariable String roomId) {
        log.info("getChatHistoryList request: {}", roomId);
        List<ChatBotMessageResponseDTO> dto = chatBotService.getChatBotMessageList(roomId);
        return ResponseEntity.ok(dto);
    }

}
