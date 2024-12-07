package com.example.ChatMicroService.webSockets;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.services.ChattingMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChattingMessageService chattingMessageService;

    public WebSocketHandler(ChattingMessageService chattingMessageService) {
        this.chattingMessageService = chattingMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getUri().getQuery().split("=")[1]; // Extrage userId din query parametru
        sessions.put(userId, session);  // Stocăm sesiunea utilizatorului
        System.out.println("User/Administrator connected: " + userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Deserializăm mesajul primit
        ChattingMessageDTO chatMessage = objectMapper.readValue(message.getPayload(), ChattingMessageDTO.class);
        System.out.println("Message received from: " + chatMessage.getSenderId() + " to: " + chatMessage.getReceiverId());

        // Dacă este un mesaj de tip "typing", actualizăm starea în frontend
        if (chatMessage.isTyping()) {
            handleTypingNotification(chatMessage);
            return;
        }

        // Salvăm mesajul în baza de date
        this.chattingMessageService.saveMessage(chatMessage);

        // Căutăm sesiunea destinatarului
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId().toString());
        if (receiverSession != null && receiverSession.isOpen()) {
            System.out.println("Sending message to: " + chatMessage.getReceiverId());
            // Trimiterea mesajului către destinatar
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else {
            System.err.println("Receiver session not found or closed for message: " + chatMessage);
        }
    }

    private void handleTypingNotification(ChattingMessageDTO chatMessage) throws Exception {
        // Transmite notificarea de "typing" către destinatar
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId().toString());
        if (receiverSession != null && receiverSession.isOpen()) {
            System.out.println("Sending typing notification to: " + chatMessage.getReceiverId());
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else {
            System.err.println("Receiver session not found or closed for typing notification: " + chatMessage);
        }
    }

    private void handleReadNotification(ChattingMessageDTO chatMessage) throws Exception {
        // Căutăm sesiunea expeditorului și trimitem notificarea de "read"
        WebSocketSession senderSession = sessions.get(chatMessage.getSenderId().toString());
        if (senderSession != null && senderSession.isOpen()) {
            chatMessage.setSeen(true); // Setează mesajul ca fiind citit
            senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Ștergem sesiunea utilizatorului la deconectare
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }
}
