package com.himedia.luckydokiapi.domain.chat.service;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import com.himedia.luckydokiapi.domain.chat.dto.ChatHistoryDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.chat.dto.MessageNotificationDTO;
import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import com.himedia.luckydokiapi.domain.chat.repository.ChatMessageRepository;
import com.himedia.luckydokiapi.domain.chat.repository.ChatRoomRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.exception.NotAccessChatRoom;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.himedia.luckydokiapi.domain.member.enums.MemberRole.SELLER;
import static com.himedia.luckydokiapi.domain.member.enums.MemberRole.USER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final MongoTemplate mongoTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;


    @Override
    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO, String email) {
        log.info("chatMessageDTO: {}", chatMessageDTO);
        //shop 조회
        Shop shop = getShop(chatMessageDTO.getShopId());
        //구매자
        Member member = getMember(email);

        return this.saveMongoAndReturnChatDTO(chatMessageDTO, member, shop);
    }


    //채팅방 아이디로 mu sql db와 mongo db 를 조회 한뒤 아이디에 해당하는 채팅방 메세지들을 전부 가져온다
    @Transactional(readOnly = true)
    @Override
    public List<ChatHistoryDTO> getChattingHistory(String email, Long roomId) {
        log.info("getChattingHistory : {}", email);
        log.info("roomId: {}", roomId);
        //회원 확인
        Member member = getMember(email);
        //채팅방이 있는지 확인
        ChatRoom chatRoom = getChatroom(roomId);

        // 파라미터로 온 이메일이 채팅방의 참여자 인지 확인
        //shop 의 셀러이거나 , 구매자의 이메일 둘 다 아니라면 ?
        if (!chatRoom.getShop().getMember().equals(member) && !chatRoom.getMember().equals(member)) {
            throw new NotAccessChatRoom("해당 채팅방의 참여자가 아닙니다.");
        }
        return getChatMessage(chatRoom);
    }


    @Override
    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email) {
        //메세지를 보내는 시점에서 채팅룸이 생성되므로 sender 의 email 로 생성
        //회원 조회
        log.info("createChatRoom : {}", chatRoomDTO);
        Member member = getMember(email);
        // shop 조회
        Shop shop = getShop(chatRoomDTO.getShopId());
        // sql ChatRoom 엔티티에 저장, id는 자동으로 생성되므로 null 로 전달

        ChatRoom chatRoom = createChatRoomEntity(member, shop, null);
        //새로운 chat room 생성 + 저장

        chatRoomRepository.save(chatRoom);
        //다시 dto로 변환하여 리턴
        return this.convertToChatRoomDTO(chatRoom, null);

    }

    //룸 아이디로 채팅방 상세 조회
    private List<ChatHistoryDTO> getChatMessage(ChatRoom chatRoom) {
        //roomId 로 mongo db 메세지 내역을 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderBySendTimeAsc(chatRoom.getId());

        if (chatMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return chatMessages.stream()
                .map(chatMessage -> ChatHistoryDTO.builder()
                        .roomId(chatRoom.getId())
                        .email(chatMessage.getEmail())
                        .ShopId(chatRoom.getShop().getId())
                        .shopImage(chatRoom.getShop().getImage())
                        .message(chatMessage.getMessage())
                        .lastMessageTime(LocalDateTime.from(chatMessage.getSendTime()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatRoomDTO> findAllChatRooms(String email) {
        log.info("findAllChatRooms : {}", email);
        Member member = getMember(email);

        //로그인한 회원의 메세지 룸 가져오기
        List<ChatRoom> chatRoomList = chatRoomRepository.findByMemberOrShopMember(member.getEmail());


        List<Long> roomIds = chatRoomList.stream().map(ChatRoom::getId).toList();
        //메세지룸의 룸 아이디들을 가져오기
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessagesByRoomIds(roomIds);
        //룸 아이디들로 마지막 메세지 찾기
//아이디 리스트 들 + 메세지 리스트 하나씩 빼서 convertToChatRoomDTO로 변환 시키고 가시 list 처리
        Map<Long, ChatMessage> lastMessageMap = lastMessages.stream()
                .filter(msg -> msg.getRoomId() != null)
                .collect(Collectors.toMap(
                        ChatMessage::getRoomId,
                        message -> message,
                        (existing, replacement) -> replacement  // 혹시 중복이 있을 경우 처리
                ));

        return chatRoomList.stream().map(chatRoom -> {
            ChatMessage lastMessage = lastMessageMap.get(chatRoom.getId());
            return convertToChatRoomDTO(chatRoom,
                    lastMessage != null ? lastMessage.getMessage() : null);
        }).toList();
    }

    @Override
    public Set<String> getRoomMembers(Long roomId) {
        return chatRoomRepository.findByChatMembers(roomId);
    }


    @Transactional
    @Override  // 읽음 상태 바꾸기
    public void changeRead(String email, Long roomId) {
        Member member = getMember(email);
        chatRoomRepository.modifyIsRead(roomId, member.getEmail());
    }

    @Override
    @Transactional(readOnly = true)//안읽은 알림 리스트
    public List<MessageNotificationDTO> getUnreadNotifications(String email) {
        Member member = getMember(email);
        //안읽은 채티방 리스트에 참여한 멤버들을 찾기
// 해당 사용자의 안 읽은 채팅방들을 가져옴
        List<ChatRoom> unreadRooms = chatRoomRepository.findByMemberAndIsRead(member.getEmail(), false);

        // 각 채팅방의 마지막 메시지 발신자 정보와 함께 DTO로 변환
        return unreadRooms.stream()
                .map(room -> {
                    // 채팅방의 마지막 메시지 발신자 찾기
                    String sender = this.getRoomMembers(room.getId()).stream()
                            .filter(roomMember -> !roomMember.equals(member.getEmail()))
                            .findFirst()
                            .orElse(null);

                    return MessageNotificationDTO.builder()
                            .roomId(room.getId())
                            .sender(sender)  // 발신자 설정
                            .email(member.getEmail())  // 수신자(현재 사용자)
                            .notificationMessage(sender + "님에게 새 메세지가 도착하였습니다")
                            .timestamp(room.getLastMessageTime())
                            .isRead(false)
                            .build();
                })
                .toList();
    }



    private Boolean getSellerAndBuyer(String email) {
        Member member = this.getMember(email);
        if (member.getMemberRoleList().contains(SELLER)) {
            return true; //핀매자
        } else if (member.getMemberRoleList().contains(USER)) {
            return false;
        }//구매자
        throw new NotAccessChatRoom("채팅 권한이 없습니다");
    }


    private Member getMember(String email) {
        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("Member with email " + email + " not found"));
    }

    private ChatRoom getChatroom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("해당 아이디의 채팅룸은 존재하지 않습니다 "));
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new EntityNotFoundException("shop with id " + shopId + " not found"));
    }


    private ChatMessageDTO saveMongoAndReturnChatDTO(ChatMessageDTO chatMessageDTO, Member member, Shop shop) {
        //채팅룸의 아이디로 엔티티 조회
        ChatRoom chatRoom = getChatroom(chatMessageDTO.getRoomId());


        // document 변환
        ChatMessage chatMessage = this.convertToDocument(chatMessageDTO, member, shop, chatRoom.getId());
        //mongodb 에 저장된 document
        mongoTemplate.save(chatMessage);
        //저장된 document 를 다시 dto 로 변환하여 전달
        return this.convertToDTO(chatMessage);
    }


}
