package com.example.ChatMicroService.controllers;

import com.example.ChatMicroService.dtos.ChattingMessageDTO;
import com.example.ChatMicroService.services.ChattingMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingMessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChattingMessageDTO messageDto) {
        System.out.println(messageDto);
        messageService.saveMessage(messageDto);

        return ResponseEntity.ok().build();
    }
}
