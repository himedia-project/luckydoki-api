//package com.himedia.luckydokiapi.domain.chat.service;
//
//import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
//
//import java.util.List;
//
//public interface ChatService {
//    ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO);
//
//
//    default ChatMessage convertToDocument(ChatMessageDTO chatMessageDTO) {
//        return ChatMessage.builder()
//                .roomId(chatMessageDTO.getRoomId())
//                .buyerEmail(chatMessageDTO.getBuyerEmail())
//                .sellerEmail(chatMessageDTO.getSellerEmail())
//                .productId(chatMessageDTO.getProductId())
//                .senderId(chatMessageDTO.getSenderId())
//                .message(chatMessageDTO.getMessage())
//                .build();
//
//    }
//
//    default ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
//        return ChatMessageDTO.builder()
//                .roomId(chatMessage.getRoomId())
//                .senderId(chatMessage.getSenderId())
//                .sellerEmail(chatMessage.getSellerEmail())
//                .buyerEmail(chatMessage.getBuyerEmail())
//                .productId(chatMessage.getProductId())
//                .message(chatMessage.getMessage())
//                .sendDate(chatMessage.getSendDate())
//                .build();
//    }
//
//    List<ChatMessageDTO> getHistory(Long roomId);
//
//    ChatRoomDTO createChatRoom(ChatMessageDTO chatMessageDTO);
//}