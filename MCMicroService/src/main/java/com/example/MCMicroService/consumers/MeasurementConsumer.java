package com.example.MCMicroService.consumers;

import com.example.MCMicroService.dtos.measurementDTOs.MeasurementDTO;
import com.example.MCMicroService.services.MeasurementService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class MeasurementConsumer {

    private static final String QUEUE_NAME = "measurementsQueue";

    private static final String RABBITMQ_URL = "amqps://tvhnxavp:oVIA224XVwZrGKOrr-GIFoqy0J5-Kj8o@rat.rmq2.cloudamqp.com/tvhnxavp";

    private final MeasurementService measurementService;

    private final ObjectMapper objectMapper;

    private Connection connection;

    private Channel channel;

    public MeasurementConsumer(MeasurementService measurementService) {
        this.measurementService = measurementService;
        this.objectMapper = new ObjectMapper();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(RABBITMQ_URL);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        } catch (Exception e) {
            System.err.println("Failed to initialize RabbitMQ consumer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void listenForMessages() {
        try {
            if (channel == null) {
                throw new IllegalStateException("Channel is not initialized");
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received: " + message);
                parseMeasurement(message);
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                System.out.println(" [x] Consumer " + consumerTag + " cancelled");
            });
        } catch (IOException e) {
            System.err.println("Failed to consume messages: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseMeasurement(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            UUID deviceId = UUID.fromString(jsonNode.get("device_id").asText());
            long timestamp = jsonNode.get("timestamp").asLong();
            double measure = jsonNode.get("measure").asDouble();

            MeasurementDTO measurementDTO = new MeasurementDTO();
            measurementDTO.setMeasure(measure);
            measurementDTO.setTimestamp(timestamp);
            measurementDTO.setDevice_id(deviceId);

            measurementService.createMeasurement(measurementDTO);
            measurementService.checkHourlyConsumption(deviceId, timestamp, true);
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            System.err.println("Failed to close RabbitMQ resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
