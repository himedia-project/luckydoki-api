//package com.himedia.luckydokiapi.domain.chat.controller;
//
//import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
//import com.himedia.luckydokiapi.domain.chat.service.ChatService;
//import com.himedia.luckydokiapi.domain.member.entity.Member;
//import com.himedia.luckydokiapi.security.MemberDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/chat")
//@RequiredArgsConstructor
//@Setter
//@Slf4j
//public class ChatController {
//    private final ChatService chatService;
//    private final SimpMessageSendingOperations messagingTemplate;
//
//
//    //메세지를 전송하는 api
//    //양방향 통신이므로 return 값이 따로 없고 messagingTemplate.convertAndSend 를 통해 stomp 형식으로 클라이언트에 전송된다
//    @MessageMapping("/message")
//    public void handleMessage(ChatMessageDTO chatMessageDTO, StompHeaderAccessor stompHeaderAccessor) {
//        log.info("Received chat message {}", chatMessageDTO);
//        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
//        Member member = (Member) authentication.getPrincipal();
//
//        chatMessageDTO.setSellerEmail(member.getEmail());
//        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessageDTO);
//
//        //구독자에게 메세지 전송
//        //클라이언트 구독주소(destination) + 채팅방 번호 + 전송되고 mongodb 애 저장될 메세지(payload)
//        //mu sql 테이블에도 insert
//        // /topic/chat/room/{roomId} 에게 전송
//        messagingTemplate.convertAndSend("/topic/chat/message" + chatMessageDTO.getRoomId(), savedMessage);
//    }
//
//    //roomId와 seller 의 email 로 채팅목록 조회
//    @GetMapping("/room/seller/{roomId}/list")
//    public ResponseEntity<List<ChatMessageDTO>> getChatHistoryBySeller(@AuthenticationPrincipal final MemberDTO memberDTO, @PathVariable Long roomId) {
//        log.info("Received room {}", roomId);
//        return ResponseEntity.ok(chatService.getSellerHistory(memberDTO.getEmail(), roomId));
//    }
//
//    //room Id + buyer(member) 조회
//    @GetMapping("/room/buyer/{roomId}/list")
//    public ResponseEntity<List<ChatMessageDTO>> getChatHistoryByBuyer(@AuthenticationPrincipal final MemberDTO memberDTO, @PathVariable Long roomId) {
//        log.info("Received room {}", roomId);
//        return ResponseEntity.ok(chatService.getBuyerHistory(memberDTO.getEmail(), roomId));
//    }
//
//    //채팅방 생성 api
//    @PostMapping
//    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody ChatMessageDTO chatMessageDTO, @AuthenticationPrincipal final MemberDTO memberDTO) {
//        log.info("Received chat message {}", chatMessageDTO);
//        return ResponseEntity.ok(chatService.createChatRoom(chatMessageDTO, memberDTO.getEmail()));
//    }
//
//
//}
