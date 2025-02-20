package com.himedia.luckydokiapi.domain.chat.service;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import com.himedia.luckydokiapi.domain.chat.dto.ChatHistoryDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.chat.dto.MessageNotificationDTO;

import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ChatService {
    ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO, String email);

    List<ChatHistoryDTO> getChattingHistory(String email, Long roomId);

    ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email);

    List<ChatRoomDTO> findAllChatRooms(String email);

    default ChatMessage convertToDocument(ChatMessageDTO chatMessageDTO, Member member, Shop shop, Long chatRoomId) {
        return ChatMessage.builder()
                .roomId(chatRoomId)
                .email(member.getEmail())
                .shopId(shop.getId())
                .message(chatMessageDTO.getMessage())
                .sendTime(chatMessageDTO.getSendTime())
                .build();

    }

    //클라이언트에 보낼 채팅 메세지 기록
    default ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .roomId(chatMessage.getRoomId())
                .ShopId(chatMessage.getShopId())
                .email(chatMessage.getEmail())
                .message(chatMessage.getMessage())
                .sendTime((chatMessage.getSendTime()))
                .build();
    }

    default ChatRoom createChatRoomEntity(Member member, Shop shop, Long chatRoomId) {
        return ChatRoom.builder()
                .id(chatRoomId) //null 이면 자동으로 생성된다
                .shop(shop)
                .member(member)
                .shopImage(shop.getImage())
                .lastMessageTime(LocalDateTime.now())
                .build();
    }

    default ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom, String message) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .member(chatRoom.getMember().getEmail())
                .shopId(chatRoom.getShop().getId())
                .shopImage(chatRoom.getShopImage())
                .shopName(chatRoom.getShop().getMember().getNickName())
                .isRead(chatRoom.getIsRead())
                .lastMessage(message == null ? null : message)
                .lastMessageTime(chatRoom.getLastMessageTime())
                .build();
    }



    Set<String> getRoomMembers(Long roomId);


    List<MessageNotificationDTO> getUnreadNotifications(String email);


    void changeRead(String email, Long roomId);

    Long deleteChatRoom(String email, Long roomId);
//    Boolean findChatRoom(String email, Long shopId);
}