package com.himedia.luckydokiapi.domain.chat.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Document(collection = "chatMessages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//mongo db에 실제 채팅 메세지 저장 , 조회
public class ChatMessage {

    //각각의 채팅 '메세지' 마다 부여되는 고유 id
    //개별 채팅 메세지를 찾을때 사용되는 값
    @Id
    private String id; // mongodb 에선 string 타입의 id 사용


    //MySQL의 ChatRoom 테이블의 ID를 참조
    private Long roomId;

    // 메시지 보낸 사람의 Member ID
    private String email;
    private Long shopId;     // 상품 ID
    private String message;     // 채팅 내용
    private LocalDateTime sendTime;
    @Builder.Default
    private boolean isRead = false;

//    public LocalDateTime getSendTimeAsInstant() {
//        return this.sendTime.atZone(ZoneOffset.UTC).toInstant();
//    }

//    public Instant toInstant() {
//        return this.sendTime != null
//                ? this.sendTime.atOffset(ZoneOffset.UTC).toInstant()
//                : null;
//    }

}
