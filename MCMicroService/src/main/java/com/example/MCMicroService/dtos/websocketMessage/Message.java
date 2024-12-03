package com.example.MCMicroService.dtos.websocketMessage;

import lombok.Data;

@Data
public class Message {
    private String type;
    private String content;
    private String userId;

    public Message(String type, String content, String userId) {
        this.type = type;
        this.content = content;
        this.userId = userId;
    }
}