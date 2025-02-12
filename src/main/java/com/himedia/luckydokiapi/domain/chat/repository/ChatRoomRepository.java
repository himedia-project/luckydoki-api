package com.himedia.luckydokiapi.domain.chat.repository;

import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
