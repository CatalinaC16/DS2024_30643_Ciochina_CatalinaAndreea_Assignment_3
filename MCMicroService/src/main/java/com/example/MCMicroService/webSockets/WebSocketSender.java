package com.example.MCMicroService.webSockets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketSender {

    private final WebSocketHandler webSocketHandler;

    public void sendAlert(String deviceId, double averageConsumption, double maxLimit, String userId) {
        String message = String.format(
                "Alert! Device %s has exceeded the hourly consumption limit. Average: %.2f, Max: %.2f",
                deviceId, averageConsumption, maxLimit
        );
        webSocketHandler.sendMessageToAll(message, userId);
    }
}
