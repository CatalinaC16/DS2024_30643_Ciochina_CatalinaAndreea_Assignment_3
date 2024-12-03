package com.example.deviceMS.producer;

import com.example.deviceMS.dtos.deviceDTOs.DeviceMessageDTO;
import com.example.deviceMS.entities.Device;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Component
public class DeviceProducer {
    private static final String EXCHANGE_NAME = "devicesTopic";
    private static final String RABBITMQ_URL = "amqps://tvhnxavp:oVIA224XVwZrGKOrr-GIFoqy0J5-Kj8o@rat.rmq2.cloudamqp.com/tvhnxavp";
    private Connection connection;
    private Channel channel;
    private final ObjectMapper objectMapper;

    public DeviceProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(RABBITMQ_URL);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
        } catch (Exception e) {
            System.err.println("Failed to initialize RabbitMQ connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendDeviceMessage(Device device, String action) {
        try {
            if (channel != null && channel.isOpen()) {
                String routingKey = "device." + action;
                String message = createMessagePayload(device, action);

                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
                System.out.println(" [x] Sent message with routingKey [" + routingKey + "]: " + message);
            } else {
                System.err.println("Channel is not open. Message not sent.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createMessagePayload(Device device, String action) throws JsonProcessingException {
        var payload = new DeviceMessageDTO(device.getId(), action, device.getMaxHourlyEnergyConsumption(), device.getUser().getId());
        return objectMapper.writeValueAsString(payload);
    }

    public void close() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            System.err.println("Failed to close RabbitMQ connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
