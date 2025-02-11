package com.himedia.luckydokiapi.domain.chat.repository;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    //jpa 레포지토리 처럼 이름규칙에 따라 자동으로 쿼리 생성

    //채팅방 id 로 조회하고 최신 시간순으로 정렬하기
    List<ChatMessage> findByIdOrderBySendDateAsc(String id);

    //seller email 로 해당 셀러의 채팅 기록 조회
    List<ChatMessage> findBySellerEmail(String sellerEmail);

    //buyer email 로 해당 유저의 채팅 문의 기록 조회
    List<ChatMessage> findByBuyerEmail(Long buyerEmail);

}
