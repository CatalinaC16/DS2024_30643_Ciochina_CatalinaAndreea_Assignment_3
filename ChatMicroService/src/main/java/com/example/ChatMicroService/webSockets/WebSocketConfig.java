package com.example.ChatMicroService.webSockets;

import com.example.ChatMicroService.services.ChattingMessageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChattingMessageService chattingMessageService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(chattingMessageService), "/chat")
                .setAllowedOrigins("*");
    }
}

