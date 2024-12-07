package com.example.ChatMicroService.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingMessageDTO {
    private UUID id;
    private String content;
    private String senderId;
    private String receiverId;
    private boolean isSeen;
    private LocalDateTime timestamp;
    private boolean typing;
}
