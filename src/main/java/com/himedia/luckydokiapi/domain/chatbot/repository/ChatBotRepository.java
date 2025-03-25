package com.himedia.luckydokiapi.domain.chatbot.repository;

import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatBotRepository extends MongoRepository<ChatbotMessage, String> {
}
