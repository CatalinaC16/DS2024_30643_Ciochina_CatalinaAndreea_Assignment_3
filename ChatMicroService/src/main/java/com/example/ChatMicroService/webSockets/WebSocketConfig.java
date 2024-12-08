package com.example.ChatMicroService.webSockets;

import com.example.ChatMicroService.dtos.mappers.ChattingMessageMapper;
import com.example.ChatMicroService.repositories.ChattingMessageRepository;
import com.example.ChatMicroService.services.ChattingMessageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChattingMessageService chattingMessageService;

    private final ChattingMessageRepository chattingMessageRepository;

    private final ChattingMessageMapper chattingMessageMapper;

    public WebSocketConfig(ChattingMessageService chattingMessageService,
                           ChattingMessageRepository chattingMessageRepository,
                           ChattingMessageMapper chattingMessageMapper) {
        this.chattingMessageService = chattingMessageService;
        this.chattingMessageRepository = chattingMessageRepository;
        this.chattingMessageMapper = chattingMessageMapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                new WebSocketHandler(chattingMessageService, chattingMessageRepository, chattingMessageMapper), "/chat"
        ).setAllowedOrigins("*");
    }
}
