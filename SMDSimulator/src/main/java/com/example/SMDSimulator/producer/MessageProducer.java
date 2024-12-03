package com.example.SMDSimulator.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class MessageProducer {

    private static final String QUEUE_NAME = "measurementsQueue";
    private static final String RABBITMQ_URL = "amqps://tvhnxavp:oVIA224XVwZrGKOrr-GIFoqy0J5-Kj8o@rat.rmq2.cloudamqp.com/tvhnxavp";
    private Connection connection;
    private Channel channel;

    public MessageProducer() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(RABBITMQ_URL);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        } catch (Exception e) {
            System.err.println("Failed to initialize RabbitMQ connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent: " + message);
            } else {
                System.err.println("Channel is not open. Message not sent.");
            }
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            if (connection != null && connection.isOpen()) connection.close();
            System.out.println("RabbitMQ connection and channel closed.");
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed to close RabbitMQ connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
