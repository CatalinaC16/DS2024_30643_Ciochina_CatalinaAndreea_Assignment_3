package com.example.ChatMicroService.services;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.dtos.mappers.ChattingMessageMapper;
import com.example.ChatMicroService.entities.Message;
import com.example.ChatMicroService.repositories.ChattingMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChattingMessageService {

    private final ChattingMessageRepository chattingMessageRepository;

    private final ChattingMessageMapper chattingMessageMapper;

    public Message saveMessage(ChattingMessageDTO message) {
        Message message1 = this.chattingMessageMapper.toEntity(message);
        return this.chattingMessageRepository.save(message1);
    }

    public List<Message> getMessages(String sender, String receiver) {
        return this.chattingMessageRepository.findAll();
    }
}
