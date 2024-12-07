package com.example.ChatMicroService.webSockets;

import com.example.ChatMicroService.services.ChattingMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChattingMessageService chattingMessageService;

    public WebSocketConfig(ChattingMessageService chattingMessageService) {
        this.chattingMessageService = chattingMessageService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(chattingMessageService), "/chat")
                .setAllowedOrigins("*"); // Permite accesul din orice origine
    }
}
