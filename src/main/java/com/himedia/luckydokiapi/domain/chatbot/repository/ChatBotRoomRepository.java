package com.himedia.luckydokiapi.domain.chatbot.repository;

import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatBotRoomRepository extends MongoRepository<ChatbotRoom, String> {
}
