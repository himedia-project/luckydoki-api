//package com.himedia.luckydokiapi.domain.chat.service;
//
//import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
//import com.himedia.luckydokiapi.domain.chat.repository.ChatRoomRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//@Slf4j
//public class ChatServiceImpl implements ChatService {
//
//    private final MongoTemplate mongoTemplate;
//    private final ChatRoomRepository chatRoomRepository;
//
//    @Override
//    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO) {
//        //dto -> document 변환
//        ChatMessage chatMessage = this.convertToDocument(chatMessageDTO);
//       //mongodb 에 저장된 document
//        ChatMessage savedChatMessage = mongoTemplate.save(chatMessage);
//        //저장된 document 를 다시 dto 로 변환하여 전달
//        return this.convertToDTO(savedChatMessage);
//    }
//
//    @Override
//    public List<ChatMessageDTO> getHistory(Long roomId) {
//        return List.of();
//    }
//
//    @Override
//    public ChatRoomDTO createChatRoom(ChatMessageDTO chatMessageDTO) {
//        return null;
//    }
//
//
//}
