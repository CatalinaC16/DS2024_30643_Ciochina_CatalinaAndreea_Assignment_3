package com.example.MCMicroService.consumers;

import com.example.MCMicroService.dtos.deviceDTOs.DeviceDTO;
import com.example.MCMicroService.services.DeviceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DeviceConsumer {

    private final DeviceService deviceService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "devicesQueue", durable = "true"),
            exchange = @Exchange(value = "devicesTopic", type = "topic"),
            key = "device.#"
    ))
    public void readMessagesFromTopic(String message) {
        System.out.println("Received message: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String action = jsonNode.get("action").asText();
            DeviceDTO device = parseDevice(jsonNode);
            switch (action) {
                case "insert":
                    deviceService.createDevice(device);
                    break;
                case "update":
                    deviceService.updateDevice(device);
                    break;
                case "delete":
                    deviceService.deleteDevice(device.getId());
                    break;
                default:
                    throw new RuntimeException("Unknown action: " + action);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static DeviceDTO parseDevice(JsonNode jsonNode) {
        DeviceDTO device = DeviceDTO.builder()
                .id(UUID.fromString(jsonNode.get("id").asText()))
                .maxHourlyEnergyConsumption(jsonNode.get("maxHourlyEnergyConsumption").intValue())
                .user_id(UUID.fromString(jsonNode.get("user_id").asText()))
                .build();
        System.out.println("Device parsed: " + device);
        return device;
    }
}
