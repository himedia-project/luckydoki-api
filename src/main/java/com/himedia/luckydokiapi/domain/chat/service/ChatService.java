package com.himedia.luckydokiapi.domain.chat.service;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import com.himedia.luckydokiapi.domain.chat.dto.ChatHistoryDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageResponseDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageRequestDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;

import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ChatService {
    ChatMessageResponseDTO saveMessage(ChatMessageRequestDTO chatMessageDTO, String email);

    List<ChatHistoryDTO> getChattingHistory(String email, Long roomId);

    ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email);

    List<ChatRoomDTO> findAllChatRooms(String email);

    default ChatMessage convertToDocument(ChatMessageRequestDTO chatMessageDTO, Member member, Shop shop, Long chatRoomId) {
        return ChatMessage.builder()
                .roomId(chatRoomId)
                .email(member.getEmail())
                .shopId(shop.getId())
                .message(chatMessageDTO.getMessage())
                .sendTime(chatMessageDTO.getSendTime())
                .build();

    }

    //클라이언트에 보낼 채팅 메세지 기록
    default ChatMessageResponseDTO convertToDTO(ChatMessage chatMessage, String sender) {
        return ChatMessageResponseDTO.builder()
                .roomId(chatMessage.getRoomId())
                .shopId(chatMessage.getShopId())
                .sender(sender)
                .email(chatMessage.getEmail())
                .message(chatMessage.getMessage())
                .sendTime((chatMessage.getSendTime()))
                .isRead(chatMessage.isRead())
                .build();
    }

    default ChatRoom createChatRoomEntity(Member member, Shop shop, Long chatRoomId) {
        return ChatRoom.builder()
                .id(chatRoomId) //null 이면 자동으로 생성된다
                .shop(shop)
                .createdAt(LocalDateTime.now())
                .member(member)
                .shopImage(shop.getImage())
                .lastMessageTime(LocalDateTime.now())
                .build();
    }

    default ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom, String sender, String message) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                //대화 상대방
                .sender(sender)
                //문의를 받은 입장의 셀러의 'shopId'
                .shopId(chatRoom.getShop().getId())
                .shopImage(chatRoom.getShopImage())
                .createdAt(chatRoom.getCreatedAt())
                .shopName(chatRoom.getShop().getMember().getNickName())
                .lastMessage(message == null ? null : message)
                .lastMessageTime(chatRoom.getLastMessageTime())
                .build();
    }


    Set<String> getRoomMembers(Long roomId);


    List<ChatMessageResponseDTO> getUnreadNotifications(String email);


    void changeRead(String email, Long roomId);

    Long deleteChatRoom(String email, Long roomId);
//    Boolean findChatRoom(String email, Long shopId);
}