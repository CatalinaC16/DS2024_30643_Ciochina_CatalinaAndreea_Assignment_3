package com.example.ChatMicroService.services;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.dtos.mappers.ChattingMessageMapper;
import com.example.ChatMicroService.entities.Message;
import com.example.ChatMicroService.repositories.ChattingMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChattingMessageService {

    private final ChattingMessageRepository chattingMessageRepository;

    private final ChattingMessageMapper chattingMessageMapper;

    public Message saveMessage(ChattingMessageDTO message) {
        Message message1 = this.chattingMessageMapper.toEntity(message);
        return this.chattingMessageRepository.save(message1);
    }

    public List<ChattingMessageDTO> getConversation(UUID userId1, UUID userId2) {
        List<Message> messages = this.chattingMessageRepository.findBySenderIdOrReceiverId(userId1, userId2);
        return messages.stream().map(this.chattingMessageMapper::toDTO).collect(Collectors.toList());
    }

    public List<ChattingMessageDTO> getConversationOfAdmin(UUID adminId) {
        List<Message> messages = this.chattingMessageRepository.findAll();
        messages = messages.stream().filter(x -> x.getReceiverId().equals(adminId)).collect(Collectors.toList());
        return messages.stream().map(this.chattingMessageMapper::toDTO).collect(Collectors.toList());
    }
}
