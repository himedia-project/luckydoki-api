package com.himedia.luckydokiapi.domain.chat.controller;

import com.himedia.luckydokiapi.domain.chat.dto.ChatHistoryDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.chat.dto.MessageNotificationDTO;
import com.himedia.luckydokiapi.domain.chat.service.ChatService;
import com.himedia.luckydokiapi.exception.NotAccessChatRoom;
import com.himedia.luckydokiapi.security.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

        //해당 룸 아이디를 구독하고 있는 유저에게 메세지 알림 전송
        Set<String> roomMembers = chatService.getRoomMembers(chatMessageDTO.getRoomId());
        for (String roomMember : roomMembers) {
            if (!roomMember.equals(member.getEmail())) { // 발신자가 아니라면 ?
                //roomMember : 알림을 받을 사람
//                MessageNotificationDTO notificationDTO = MessageNotificationDTO.builder()
//                        .notificationMessage("새 메세지가 도차했습니다")
//                        .roomId(chatMessageDTO.getRoomId())
//                        .timestamp(chatMessageDTO.getSendTime())
//                        .email(roomMember)
//                        .isRead(false)
//                        .build();

                messagingTemplate.convertAndSendToUser(roomMember, "/queue/notification/", chatMessageDTO);
                log.info("roomMember {} , 알림 수신 완료 ", roomMember);
            } ///queue 는 1;1 개인 알림 메세지에 사용됨
            // 프론트 단에서는 /queue/notification/ 앞에 user (roomMember)가 추가됨
        }

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

    //안읽은 알림 리스트 /history 와 다른점 : isRead 값 false 인걸로 조회
    @GetMapping("/notifications")
    public ResponseEntity<List<MessageNotificationDTO>> getMessageNotifications(@AuthenticationPrincipal final MemberDTO memberDTO) {
        log.info("memberDTO {}", memberDTO);
        return ResponseEntity.ok(chatService.getUnreadNotifications(memberDTO.getEmail()));
    }

    //읽음 상태 바꾸기
    @PatchMapping("/{roomId}")
    public ResponseEntity<?> changeReadStatus(@AuthenticationPrincipal final MemberDTO memberDTO, @PathVariable Long roomId) {
        log.info("notificationId {}", roomId);
        chatService.changeRead(memberDTO.getEmail(), roomId);
        return ResponseEntity.ok().build();
    }

    //대화방 나가기
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Long> deleteChatRoom(@AuthenticationPrincipal final MemberDTO memberDTO, @PathVariable Long roomId) {
        log.info("memberDTO {}", memberDTO);
        Long deleteRoomId = chatService.deleteChatRoom(memberDTO.getEmail(), roomId);
        return ResponseEntity.ok(deleteRoomId);
    }
//    @GetMapping
//    public ResponseEntity<Boolean> existsChatRoom(@AuthenticationPrincipal final MemberDTO memberDTO, @RequestParam Long shopId) {
//        log.info("memberDTO {}", memberDTO);
//        log.info("shopId {}", shopId);
//        return ResponseEntity.ok(chatService.findChatRoom(memberDTO.getEmail(), shopId));
//    }

}//TODO: 새메세지 알림 시 seller images 보내기 + 마지막 메세지 같이 출력
//TODO: 안읽은 알림 리스트 빼고 그냥 채팅 리스트를 드랍다운 칸에다 넣어도 될거같음 (어차피 isRead 값만 다릅니다), 메세지 드랍다운 칸은 어차피 실시간 알람이 가면 보낸 메세지로 가므로 ..
// 알림 띄워봤는데 서버 로고에는 잘 뜨지만 클라쪽 디버깅 콘솔로그가 안떠서 확인이 필요함
