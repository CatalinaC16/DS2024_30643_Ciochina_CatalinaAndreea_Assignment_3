package com.example.MCMicroService.consumers;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsumersRunner implements CommandLineRunner {

    private final MeasurementConsumer measurementConsumer;

    @Override
    public void run(String... args) throws Exception {
        measurementConsumer.listenForMessages();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumer...");
            measurementConsumer.close();
        }));
    }
}
