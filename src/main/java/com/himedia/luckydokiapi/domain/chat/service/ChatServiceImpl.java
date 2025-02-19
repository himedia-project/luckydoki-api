package com.himedia.luckydokiapi.domain.chat.service;

import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
import com.himedia.luckydokiapi.domain.chat.dto.ChatHistoryDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import com.himedia.luckydokiapi.domain.chat.repository.ChatMessageRepository;
import com.himedia.luckydokiapi.domain.chat.repository.ChatRoomRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import com.himedia.luckydokiapi.exception.NotAccessChatRoom;
import com.himedia.luckydokiapi.security.MemberDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        Member member = getMember(email);
        // shop 조회
        Shop shop = getShop(chatRoomDTO.getShopId());
        // sql ChatRoom 엔티티에 저장, id는 자동으로 생성되므로 null 로 전달
        ChatRoom chatRoom = createChatRoomEntity(member, shop, null);
        //새로운 chat room 생성 + 저장

        chatRoomRepository.save(chatRoom);
        //다시 dto로 변환하여 리턴
        return this.convertToChatRoomDTO(chatRoom, null);

        //존재한다면 ? 채팅 메세지만 mongo db 에 저장한다


    }

    //룸 아이디로 채팅방 상세 조회
    private List<ChatHistoryDTO> getChatMessage(ChatRoom chatRoom) {
        //roomId 로 mongo db 메세지 내역을 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderBySendTimeAsc(chatRoom.getId());

        if (chatMessages.isEmpty()) {
            return Collections.emptyList();
        }
        String shopImage = chatRoom.getShop().getImage();

        return chatMessages.stream()
                .map(chatMessage -> ChatHistoryDTO.builder()
                        .roomId(chatRoom.getId())
                        .email(chatMessage.getEmail())
                        .ShopId(chatRoom.getShop().getId())
                        .shopImage(shopImage)
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

//    @Override
//    public Boolean findChatRoom(String email, Long shopId) {
//        Member member = getMember(email);
//        Boolean exists = chatRoomRepository.chatRoomExist(member.getEmail(), shopId);
//        return exists;
//    }


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
