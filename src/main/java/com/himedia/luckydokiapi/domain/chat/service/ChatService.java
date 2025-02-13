//package com.himedia.luckydokiapi.domain.chat.service;
//
//import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
//import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
//import com.himedia.luckydokiapi.domain.member.entity.Member;
//import com.himedia.luckydokiapi.domain.product.entity.Product;
//import com.himedia.luckydokiapi.security.MemberDTO;
//
//import java.util.List;
//
//public interface ChatService {
//    ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO);
//
//    List<ChatMessageDTO> getSellerHistory(String email, Long roomId);
//
//    ChatRoomDTO createChatRoom(ChatMessageDTO chatMessageDTO, String email);
//
//    default ChatMessage convertToDocument(ChatMessageDTO chatMessageDTO, Member seller, Member buyer, Member sender, Product product, Long chatRoomId) {
//        return ChatMessage.builder()
//                .roomId(chatRoomId)
//                .sellerEmail(seller.getEmail())
//                .buyerEmail(buyer.getEmail())
//                .sender(sender.getEmail())
//                .productId(product.getId())
//                .message(chatMessageDTO.getMessage())
//                .build();
//
//    }
//
//    default ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
//        return ChatMessageDTO.builder()
//                .roomId(chatMessage.getRoomId())
//                .sellerEmail(chatMessage.getSellerEmail())
//                .buyerEmail(chatMessage.getBuyerEmail())
//                .productId(chatMessage.getProductId())
//                .message(chatMessage.getMessage())
//                .sendDate(chatMessage.getSendDate())
//                .build();
//    }
//
//    default ChatRoom createChatRoomEntity(Member buyer, Member seller, Product product, Long chatRoomId) {
//        return ChatRoom.builder()
//                .id(chatRoomId)
//                .seller(seller)
//                .buyer(buyer)
//                .product(product)
//                .build();
//    }
//
//    default ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom) {
//        return ChatRoomDTO.builder()
//                .id(chatRoom.getId())
//                .sellerEmail(chatRoom.getSeller().getEmail())
//                .buyerEmail(chatRoom.getBuyer().getEmail())
//                .productId(chatRoom.getProduct().getId())
//                .productName(chatRoom.getProduct().getName())
//                .createdAt(chatRoom.getCreatedAt())
//                .build();
//
//    }
//
//    List<ChatMessageDTO> getBuyerHistory(String email, Long roomId);
//}