package com.himedia.luckydokiapi.domain.chatbot.service;


import com.himedia.luckydokiapi.domain.chatbot.repository.ChatBotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {

    private final ChatBotRepository chatBotRepository;

}
