package com.himedia.luckydokiapi.domain.chat.controller;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import com.himedia.luckydokiapi.domain.chat.dto.ChatHistoryDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.chat.service.ChatService;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.exception.NotAccessChatRoom;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;


    //메세지를 전송하는 api
    //양방향 통신이므로 return 값이 따로 없고 messagingTemplate.convertAndSend 를 통해 stomp 형식으로 클라이언트에 전송된다
    @MessageMapping("/message")
    public void handleMessage(ChatMessageDTO chatMessageDTO, StompHeaderAccessor stompHeaderAccessor) {
        log.info("chatMessageDTO {}", chatMessageDTO);
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        MemberDTO member = (MemberDTO) authentication.getPrincipal();
        // roomId가 null 인 경우 (첫 메시지) 채팅방 생성 후 메시지 저장
        // 생성된 채팅방 ID 설정
        ChatMessageDTO savedMessage = chatService.saveMessage(chatMessageDTO, member.getEmail());
        messagingTemplate.convertAndSend("/topic/chat/message/" + chatMessageDTO.getRoomId(), savedMessage);
        //구독자에게 메세지 전송
        //클라이언트 구독주소(destination) + 채팅방 번호 + 전송되고 mongodb 애 저장될 메세지(payload)
        //mu sql 테이블에도 insert
        // /topic/chat/room/{roomId} 에게 전송

    }

    //roomId와 email 로 채팅 대화내역  상세 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<ChatHistoryDTO>> getChatHistory(@AuthenticationPrincipal final MemberDTO memberDTO,
                                                               @PathVariable Long roomId) {
        log.info("roomId {}", roomId);
        log.info("memberDTO {}", memberDTO);
        return ResponseEntity.ok(chatService.getChattingHistory(memberDTO.getEmail(), roomId));
    }

    //유저의 채팅방 리스트 보기 확인 완료
    @GetMapping("/history")
    public ResponseEntity<List<ChatRoomDTO>> getChatRooms(@AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("memberDTO {}", memberDTO);
        return ResponseEntity.ok(chatService.findAllChatRooms(memberDTO.getEmail()));
    }

    //확인 완료
    //초기 채팅방 생성
    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestBody ChatRoomDTO chatRoomDTO, @AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("chatRoomDTO {}", chatRoomDTO);
        log.info("memberDTO {}", memberDTO);
        //로그인 한 회원 (보낸 회원의 이메일을 회원 필드에 주입)
        chatRoomDTO.setMember(memberDTO.getEmail());
        if (chatRoomDTO.getId() != null) {
            throw new NotAccessChatRoom("이미 존재하는 채팅방 입니다 ");
        }
        ChatRoomDTO newRoom = chatService.createChatRoom(chatRoomDTO, memberDTO.getEmail());
        return ResponseEntity.ok(newRoom);
    }

//    @GetMapping
//    public ResponseEntity<Boolean> existsChatRoom(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestParam Long shopId) {
//        log.info("memberDTO {}", memberDTO);
//        log.info("shopId {}", shopId);
//        return ResponseEntity.ok(chatService.findChatRoom(memberDTO.getEmail(), shopId));
//    }

}