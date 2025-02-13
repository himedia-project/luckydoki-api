//package com.himedia.luckydokiapi.domain.chat.service;
//
//import com.himedia.luckydokiapi.domain.chat.document.ChatMessage;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatMessageDTO;
//import com.himedia.luckydokiapi.domain.chat.dto.ChatRoomDTO;
//import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
//import com.himedia.luckydokiapi.domain.chat.repository.ChatMessageRepository;
//import com.himedia.luckydokiapi.domain.chat.repository.ChatRoomRepository;
//import com.himedia.luckydokiapi.domain.member.entity.Member;
//import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
//import com.himedia.luckydokiapi.domain.product.entity.Product;
//import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
//import com.himedia.luckydokiapi.exception.NotAccessChatRoom;
//import com.himedia.luckydokiapi.security.MemberDTO;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static com.himedia.luckydokiapi.domain.member.enums.MemberRole.SELLER;
//import static com.himedia.luckydokiapi.domain.member.enums.MemberRole.USER;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//@Slf4j
//public class ChatServiceImpl implements ChatService {
//
//    private final MongoTemplate mongoTemplate;
//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatMessageRepository chatMessageRepository;
//    private final MemberRepository memberRepository;
//    private final ProductRepository productRepository;
//
//    @Override
//    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO) {
//        log.info("chatMessageDTO: {}", chatMessageDTO);
//        Member sender = getMember(chatMessageDTO.getSender());
//        //보낸이가 구매자라면?
//        if (!sender.getEmail().equals(chatMessageDTO.getSellerEmail())) {
//            //구매자의 채팅으로 저장 ㅎㅎ
//            return this.saveMongoAndReturnChatDTO(chatMessageDTO);
//        }
//        return this.saveMongoAndReturnChatDTO(chatMessageDTO);
//    }
//
//
//    //채팅방 아이디로 mu sql db와 mongo db 를 조회 한뒤 아이디에 해당하는 채팅방 메세지들을 전부 가져온다
//    @Transactional(readOnly = true)
//    @Override
//    public List<ChatMessageDTO> getSellerHistory(String email, Long roomId) {
//        log.info("getSellerHistory: {}", email, roomId);
//        Member seller = getSellerAndBuyer(email);
//        //회원 확인
//        ChatRoom chatRoom = getChatroom(roomId);
//        //채팅방이 있는지 확인
//        if (!seller.getSellerChatRooms().contains(chatRoom)) {
//            throw new NotAccessChatRoom("채팅목록을 조회할 자격이 없습니다 ");
//        }
//        return getChatMessage(chatRoom);
//    }
//
//    //분기처리 하려다가 너무 복잡해서 포기함 ㅋ
//    @Transactional(readOnly = true)
//    @Override
//    public List<ChatMessageDTO> getBuyerHistory(String email, Long roomId) {
//        log.info("getSellerHistory: {}", email, roomId);
//        Member seller = getSellerAndBuyer(email);
//        //회원 확인
//        ChatRoom chatRoom = getChatroom(roomId);
//        //채팅방이 있는지 확인
//        if (!seller.getSellerChatRooms().contains(chatRoom)) {
//            throw new NotAccessChatRoom("채팅목록을 조회할 자격이 없습니다 ");
//        }
//        return getChatMessage(chatRoom);
//    }
//
//    @Override
//    public ChatRoomDTO createChatRoom(ChatMessageDTO chatMessageDTO, String email) {
//        //메세지를 보내는 시점에서 채팅룸이 생성되므로 sender 의 아이디로 생성되는게 맞음
//        Member sellerAndBuyer = getSellerAndBuyer(email);
//        //메세지를 보내는 사람이 판매자라면?
//        if (!sellerAndBuyer.getEmail().equals(chatMessageDTO.getBuyerEmail())) {
//            return this.saveNewChatroom(chatMessageDTO);
//        }
//        return this.saveNewChatroom(chatMessageDTO);
//    }
//
//    private ChatRoomDTO saveNewChatroom(ChatMessageDTO chatMessageDTO) {
//        Product product = getProduct(chatMessageDTO.getProductId());//상품 조회
//        Member seller = getSellerAndBuyer(chatMessageDTO.getSellerEmail()); // 판매자 조회
//        Member buyer = getMember(chatMessageDTO.getBuyerEmail()); // 구매자 조회
//        Member sender = getMember(chatMessageDTO.getSender());
//        //채팅방 아이디가 빈값일 경우 -> 차음 메세지를 보낼때 아이디가 생성됨
//        if (chatMessageDTO.getRoomId() == null) {
//            //새로운 채팅방 아이디 생성
//            Long roomId = this.createChatRoomId();
//            //mongodb 에 채팅 메세지  저장
//            mongoTemplate.save(this.convertToDocument(chatMessageDTO, buyer, seller, sender, product, roomId));
//            //채팅방 생성하여 sql ChatRoom 엔티티에 저장
//            ChatRoom chatRoom = this.createChatRoomEntity(buyer, seller, product, roomId);
//
//            chatRoomRepository.save(chatRoom);
//
//            return this.convertToChatRoomDTO(chatRoom);
//        }
//        //
//        ChatRoom chatRoom = this.getChatroom(chatMessageDTO.getRoomId());
//        mongoTemplate.save(this.convertToDocument(chatMessageDTO, chatRoom.getBuyer(), chatRoom.getSeller(), sender, chatRoom.getProduct(), chatRoom.getId()));
//        //sql 엔티티에 chatroom 생성
//        chatRoomRepository.save(chatRoom);
//        return this.convertToChatRoomDTO(chatRoom);
//
//    }
//
//
//    private Member getSellerAndBuyer(String email) {
//        Member member = this.getMember(email);
//        if (member.getMemberRoleList().contains(SELLER)) {
//            return member;
//        } else if (member.getMemberRoleList().contains(USER)) {
//            return member;
//        }
//        throw new NotAccessChatRoom("채팅 권한이 없습니다");
//    }
//
//
//    private Member getMember(String email) {
//        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("Member with email " + email + " not found"));
//    }
//
//    private ChatRoom getChatroom(Long roomId) {
//        return chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("해당 아이디의 채팅룸은 존재하지 않습니다 "));
//    }
//
//    private Product getProduct(Long productId) {
//        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));
//    }
//
//    //chatRoom Id 생성
//    private Long createChatRoomId() {
//        Long id = 0L;
//        for (int i = 0; i < 10; i++) {
//            id = (long) (Math.random() + i);
//        }
//        return id;
//    }
//
//
//    private List<ChatMessageDTO> getChatMessage(ChatRoom chatRoom) {
//        List<ChatMessage> chatMessages = chatMessageRepository.findByIdOrderBySendDateAsc(chatRoom.getId());
//        return chatMessages.stream().map(this::convertToDTO).toList();
//
//    }
//
//    private ChatMessageDTO saveMongoAndReturnChatDTO(ChatMessageDTO chatMessageDTO) {
//        Member seller = getMember(chatMessageDTO.getSellerEmail());
//        Member buyer = getMember(chatMessageDTO.getBuyerEmail());
//        Member sender = getMember(chatMessageDTO.getSender());
//        Product product = getProduct(chatMessageDTO.getProductId());
//        //dto -> document 변환
//        ChatMessage chatMessage = this.convertToDocument(chatMessageDTO, seller, buyer, sender, product, chatMessageDTO.getRoomId());
//        //mongodb 에 저장된 document
//        ChatMessage savedChatMessage = mongoTemplate.save(chatMessage);
//
//        //저장된 document 를 다시 dto 로 변환하여 전달
//        return this.convertToDTO(savedChatMessage);
//    }
//
//
//    private boolean isSeller(Member member) {
//        return member.getMemberRoleList().contains(SELLER);
//    }
//
//    private boolean isBuyer(Member member) {
//        return member.getMemberRoleList().contains(USER);
//    }
//}
