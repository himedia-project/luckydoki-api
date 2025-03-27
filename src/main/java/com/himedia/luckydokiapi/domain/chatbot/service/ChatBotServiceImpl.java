package com.himedia.luckydokiapi.domain.chatbot.service;


import com.himedia.luckydokiapi.domain.chatbot.document.ChatbotRoom;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotMessageResponseDTO;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotRoomResponseDTO;
import com.himedia.luckydokiapi.domain.chatbot.dto.ChatBotSearchDTO;
import com.himedia.luckydokiapi.domain.chatbot.repository.ChatBotMessageRepository;
import com.himedia.luckydokiapi.domain.chatbot.repository.ChatBotRoomRepository;
import com.himedia.luckydokiapi.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatBotServiceImpl implements ChatBotService {

    private final ChatBotRoomRepository chatBotRoomRepository;
    private final ChatBotMessageRepository chatBotMessageRepository;

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<ChatBotRoomResponseDTO> getRoomList(ChatBotSearchDTO requestDTO) {
        log.info("getRoomList requestDTO: {}", requestDTO);

        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,  //페이지 시작 번호가 0부터 시작하므로
                requestDTO.getSize(),
                "asc".equals(requestDTO.getSort()) ?  // 정렬 조건
                        Sort.by("id").ascending() : Sort.by("id").descending()
        );

        Page<ChatbotRoom> result;
        
        // searchKeyword가 있는 경우와 없는 경우 구분하여 처리
        if (requestDTO.getSearchKeyword() != null && !requestDTO.getSearchKeyword().isEmpty()) {
            // 이메일, ID, 세션ID로 검색
            result = chatBotRoomRepository.searchByKeywordAlternative(
                    requestDTO.getSearchKeyword(), pageable);
        } else {
            result = chatBotRoomRepository.findAll(pageable);
        }

        return PageResponseDTO.<ChatBotRoomResponseDTO>withAll()
                .dtoList(result.stream().map(ChatBotRoomResponseDTO::toDto).toList())
                .totalCount(result.getTotalElements())
                .pageRequestDTO(requestDTO)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatBotMessageResponseDTO> getChatBotMessageList(String roomId) {
        // 해당 chatbotRoomId 의 메시지 리스트 조회
        return chatBotMessageRepository.findByChatbotRoomId(roomId).stream()
                .map(ChatBotMessageResponseDTO::toDto)
                .toList();
    }
}
