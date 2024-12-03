package com.example.MCMicroService.webSockets;

import com.example.MCMicroService.dtos.websocketMessage.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("New WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    public void sendMessageToAll(String message, String userId) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    Message alertMessage = new Message("Alert", message, userId);
                    String jsonMessage = objectMapper.writeValueAsString(alertMessage);
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            } catch (Exception e) {
                System.err.println("Error sending message: " + e.getMessage());
            }
        }
    }
}
