package com.himedia.luckydokiapi.domain.chat.repository;

import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select e from ChatRoom e where e.member.email=:email")
    List<ChatRoom> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM ChatRoom c WHERE c.member.email = :email OR c.shop.member.email = :email")
    List<ChatRoom> findByMemberOrShopMember(@Param("email") String email);
}
//
