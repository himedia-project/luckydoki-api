package com.himedia.luckydokiapi.domain.chat.repository;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
//jpa 레포지토리 처럼 이름규칙에 따라 자동으로 쿼리 생성

    //채팅방 id 로 조회하고 최신 시간순으로 정렬하기
    List<ChatMessage> findByRoomIdOrderBySendTimeAsc(Long roomId);

    List<ChatMessage> findByEmail(String email);

    @Query(value = "{ 'roomId': ?0 }", sort = "{ 'sendTime': -1 }")
    Optional<ChatMessage> findLastMessageByRoomId(@Param("roomId") Long roomId);

    @Aggregation(pipeline = {
            "{ $match: { 'roomId': { $in: ?0 } } }",
            "{ $sort: { 'sendTime': -1 } }",
            "{ $group: { " +
                    "'_id': '$roomId', " +
                    "'document': { $first: '$$CURRENT' } " +  // 현재 문서 전체를 가져옴
                    "} }",
            "{ $replaceRoot: { 'newRoot': '$document' } }"  // 원래 문서 구조로 복원
    })
    List<ChatMessage> findLastMessagesByRoomIds(@Param("roomIds") List<Long> roomIds);


    List<ChatMessage> findByShopId(Long shopId);
}



