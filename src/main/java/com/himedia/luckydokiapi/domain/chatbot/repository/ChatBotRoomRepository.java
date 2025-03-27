package com.himedia.luckydokiapi.domain.chatbot.repository;

import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatBotRoomRepository extends MongoRepository<ChatbotRoom, String> {
    
    // 이메일, ID, 세션ID로 검색하는 메서드 TODO: 검색 구조 실패
    @Query("{ $or: [" +
            "{ 'userEmail': { $regex: ?0, $options: 'i' } }, " +
            "{ 'id': { $regex: ?0, $options: 'i' } }, " +
            "{ 'sessionId': { $regex: ?0, $options: 'i' } }" +
            "] }")
    Page<ChatbotRoom> searchByKeywordAlternative(@Param("searchKeyword") String searchKeyword, Pageable pageable);
}
