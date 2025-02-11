package com.himedia.luckydokiapi.domain.chat.controller;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Setter
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;


    //메세지를 전송하는 api
    //양방향 통신이므로 return 값이 따로 없고 messagingTemplate.convertAndSend 를 통해 stomp 형식으로 클라이언트에 전송된다
    @MessageMapping("/message")
    public void handleMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessageDTO);

        //구독자에게 메세지 전송
        //클라이언트 구독주소(destination) + 채팅방 번호 + 전송되고 mongodb 애 저장될 메세지(payload)
        // /topic/chat/room/{roomId} 에게 전송
        messagingTemplate.convertAndSend("/topic/chat/message" + chatMessageDTO.getRoomId(), savedMessage);
    }

    //채팅목록 조회
    @GetMapping("/chat/room/message/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getHistory(roomId));

    }

    //채팅방 생성 api
    @PostMapping("/create/room")
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody ChatMessageDTO chatMessageDTO) {
        return ResponseEntity.ok(chatService.createChatRoom(chatMessageDTO));
    }


}
