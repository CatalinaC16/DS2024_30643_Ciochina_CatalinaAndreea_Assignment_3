package com.example.ChatMicroService.webSockets;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.services.ChattingMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ChattingMessageService chattingMessageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChattingMessageDTO chatMessage = objectMapper.readValue(message.getPayload(), ChattingMessageDTO.class);
        this.chattingMessageService.saveMessage(chatMessage);

        for (WebSocketSession wsSession : sessions.values()) {
            if (wsSession.isOpen()) {
                wsSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            }
        }
    }
}
