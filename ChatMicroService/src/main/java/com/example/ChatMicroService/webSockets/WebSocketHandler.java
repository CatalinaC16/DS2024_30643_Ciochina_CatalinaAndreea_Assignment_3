package com.example.ChatMicroService.webSockets;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.dtos.mappers.ChattingMessageMapper;
import com.example.ChatMicroService.entities.Message;
import com.example.ChatMicroService.repositories.ChattingMessageRepository;
import com.example.ChatMicroService.services.ChattingMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ChattingMessageService chattingMessageService;

    private final ChattingMessageRepository chattingMessageRepository;

    private final ChattingMessageMapper chattingMessageMapper;

    public WebSocketHandler(ChattingMessageService chattingMessageService,
                            ChattingMessageRepository chattingMessageRepository,
                            ChattingMessageMapper chattingMessageMapper) {
        this.chattingMessageService = chattingMessageService;
        this.chattingMessageRepository = chattingMessageRepository;
        this.chattingMessageMapper = chattingMessageMapper;
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getUri().getQuery().split("=")[1];
        this.sessions.put(userId, session);
        System.out.println("User/Administrator connected: " + userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChattingMessageDTO chatMessage = this.objectMapper.readValue(message.getPayload(), ChattingMessageDTO.class);
        System.out.println("Message received : " + chatMessage.toString());

        if (chatMessage.isSeen()) {
            this.markAsSeen(chatMessage.getId());
            return;
        }

        if (chatMessage.isTyping() || chatMessage.getContent().equals("")) {
            this.handleTypingNotification(chatMessage);
            return;
        }

        if ("GROUP".equals(chatMessage.getReceiverId())) {
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                if (!entry.getKey().equals(chatMessage.getSenderId().toString()) && entry.getValue().isOpen()) {
                    entry.getValue().sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                }
            }
            return;
        }

        Message messageSaved = chattingMessageService.saveMessage(chatMessage);
        ChattingMessageDTO messageToSend = this.chattingMessageMapper.toDTO(messageSaved);
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            System.out.println(messageSaved);
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToSend)));
        } else {
            System.err.println("Receiver session not found or closed for message: " + messageToSend);
        }
    }

    private void handleTypingNotification(ChattingMessageDTO chatMessage) throws Exception {
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId().toString());
        if (receiverSession != null && receiverSession.isOpen()) {
            System.out.println("Sending typing notification to: " + chatMessage.getReceiverId());
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else {
            System.err.println("Receiver session not found or closed for typing notification: " + chatMessage);
        }
    }

    public void markAsSeen(UUID messageId) {
        Message message = this.chattingMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));
        message.setSeen(true);
        this.chattingMessageRepository.save(message);

        WebSocketSession senderSession = this.sessions.get(message.getSenderId().toString());
        if (senderSession != null && senderSession.isOpen()) {
            ChattingMessageDTO readNotification = this.chattingMessageMapper.toDTO(message);
            readNotification.setSeen(true);
            try {
                senderSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(readNotification)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }
}
