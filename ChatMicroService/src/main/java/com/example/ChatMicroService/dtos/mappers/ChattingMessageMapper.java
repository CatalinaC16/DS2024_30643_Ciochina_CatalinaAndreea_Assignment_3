package com.example.ChatMicroService.dtos.mappers;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.entities.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ChattingMessageMapper {
    public ChattingMessageDTO toDTO(Message message) {
        return ChattingMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(String.valueOf(message.getSenderId()))
                .receiverId(String.valueOf(message.getReceiverId()))
                .isSeen(message.isSeen())
                .timestamp(message.getTimestamp())
                .typing(message.isTyping())
                .build();
    }

    public Message toEntity(ChattingMessageDTO dto) {
        return Message.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .senderId(UUID.fromString(dto.getSenderId()))
                .receiverId(UUID.fromString(dto.getReceiverId()))
                .isSeen(dto.isSeen())
                .timestamp(LocalDateTime.now())
                .typing(dto.isTyping())
                .build();
    }
}
