package com.himedia.luckydokiapi.domain.chat.repository;

import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select e from ChatRoom e where e.member.email=:email or e.shop.member.email =:email")
    List<ChatRoom> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM ChatRoom c WHERE c.member.email = :email OR c.shop.member.email = :email")
    List<ChatRoom> findByMemberOrShopMember(@Param("email") String email);

    @Query("SELECT c FROM ChatRoom c WHERE c.member.email = :email OR c.shop.member.email = :email ORDER BY c.lastMessageTime DESC")
    List<ChatRoom> findByMemberOrShopMemberOrderByLastMessageTimeDesc(String email);


    @Query("select e from ChatRoom e where e.member.email=:email or e.shop.member.email =:email and e.id =:roomId")
    Optional<ChatRoom> findChatRoom(@Param("email") String email, @Param("roomId") Long roomId);


    @Query("SELECT r.member.email FROM ChatRoom r WHERE r.id = :roomId " +
            "UNION " +
            "SELECT s.member.email FROM ChatRoom r JOIN r.shop s WHERE r.id = :roomId")
    Set<String> findByChatMembers(@Param("roomId") Long roomId);








    @Modifying
    @Query("delete ChatRoom c where c.member.email =:email and c.id =:roomId")
    void deleteByRoomIdAndEmail(@Param("email") String email, @Param("roomId") Long roomId);

}

