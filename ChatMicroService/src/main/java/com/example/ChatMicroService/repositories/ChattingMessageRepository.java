package com.example.ChatMicroService.repositories;

import com.example.ChatMicroService.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChattingMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySenderIdOrReceiverId(String senderId, String receiverId);
}
