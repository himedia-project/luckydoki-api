package com.himedia.luckydokiapi.domain.chatbot.repository;

import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatBotMessageRepository extends MongoRepository<ChatbotMessage, String> {

    @Query("{ 'chatRoomId' : ?0 }")
    List<ChatbotMessage> findByChatbotRoomId(@Param("roomId") String roomId);

}
