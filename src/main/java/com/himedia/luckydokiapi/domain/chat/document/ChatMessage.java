//package com.himedia.luckydokiapi.domain.chat.document;
//
//import jakarta.persistence.Id;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//
//@Document(collection = "chatMessages")
//@Getter
//@NoArgsConstructor
//
////mongo db에 실제 채팅 메세지 저장 , 조회
//public class ChatMessage {
//    @Id
//    //각각의 채팅 '메세지' 마다 부여되는 고유 id
//    //개별 채팅 메세지를 찾을때 사용되는 값
//    private String id; // mongodb 에선 string 타입의 id 사용
//
//
//    //MySQL의 ChatRoom 테이블의 ID를 참조
//    private Long roomId;
//
//
//    // 메시지 보낸 사람의 Member ID
//    private String sender;  // 보낸이
//    private String sellerEmail;
//    private String buyerEmail;
//    private Long productId;     // 상품 ID
//    private String message;     // 채팅 내용
//    private LocalDateTime sendDate;
//
//
//    @Builder
//    public ChatMessage(Long roomId, String sellerEmail, String buyerEmail, String sender
//            , Long productId, String message) {
//        //채팅 메세지 생성 메서드
//        this.roomId = roomId;
//        this.sender = sender;
//        this.sellerEmail = sellerEmail;
//        this.buyerEmail = buyerEmail;
//        this.productId = productId;
//        this.message = message;
//        this.sendDate = LocalDateTime.now();
//    }
//
//}
