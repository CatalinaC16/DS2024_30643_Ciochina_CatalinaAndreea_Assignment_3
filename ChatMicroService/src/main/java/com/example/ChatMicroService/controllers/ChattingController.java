package com.example.ChatMicroService.controllers;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.services.ChattingMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingMessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChattingMessageDTO messageDto) {
        System.out.println(messageDto);
        this.messageService.saveMessage(messageDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getConversation/{userId1}/{userId2}")
    public List<ChattingMessageDTO> getConversation(@PathVariable UUID userId1, @PathVariable UUID userId2) {
        return this.messageService.getConversation(userId1, userId2);
    }

    @GetMapping("/getByAdminId/{adminId}")
    public List<ChattingMessageDTO> getConversationOfAdmin(@PathVariable UUID adminId) {
        return this.messageService.getConversationOfAdmin(adminId);
    }
}
