//package com.himedia.luckydokiapi.domain.chat.entity;
//
//import com.himedia.luckydokiapi.domain.member.entity.Member;
//import com.himedia.luckydokiapi.domain.product.entity.Product;
//import jakarta.persistence.*;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//public class ChatRoom {
//    //mysql 에 채팅방 (chatroom) 정보 저장
//
//    //채팅방 순번
//    //chatMessage 의 roomId 와 매핑
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    //한명의 셀러는 여러 채팅방을 꾸릴 수 있음
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "seller_email")
//    private Member seller;
//
//    //한명의 유저도 여러 셀러에게 채팅을 보낼 수 있음
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "buyer_email")
//    private Member buyer;
//
//    //하나의 프로덕트에 관련하여 여러 채팅방이 생길 수 있음
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    private LocalDateTime createdAt;
//}
