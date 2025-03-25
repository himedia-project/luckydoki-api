package com.himedia.luckydokiapi.domain.chatbot.controller;

import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotSearchDTO;
import com.himedia.luckydokiapi.domain.chatbot.service.ChatBotService;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/admin/chatbot")
@RestController
@RequiredArgsConstructor
public class AdminChatBotController {

    private final ChatBotService chatBotService;

/*    @GetMapping("/room/list")
    public ResponseEntity<PageResponseDTO<ChatBotRoomResponseDTO>> getRoomList(ChatBotSearchDTO request) {
        return ResponseEntity.ok(chatBotService.getRoomList());
    }*/
}
