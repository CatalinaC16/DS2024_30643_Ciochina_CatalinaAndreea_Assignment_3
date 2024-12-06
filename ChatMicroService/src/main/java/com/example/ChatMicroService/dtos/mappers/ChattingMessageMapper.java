package com.example.ChatMicroService.dtos.mappers;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.entities.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChattingMessageMapper {
    public ChattingMessageDTO toDTO(Message message) {
        return ChattingMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .isSeen(message.isSeen())
                .timestamp(message.getTimestamp())
                .build();
    }

    public Message toEntity(ChattingMessageDTO dto) {
        return Message.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .isSeen(dto.isSeen())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
