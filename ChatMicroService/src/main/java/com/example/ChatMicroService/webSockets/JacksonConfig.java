package com.example.ChatMicroService.webSockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Înregistrăm JavaTimeModule
        objectMapper.findAndRegisterModules();  // Înregistrează toate modulele Jackson disponibile
        return objectMapper;
    }
}
