package com.userMS.UserMicroService.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final RestTemplate restTemplate;

    //private final String deviceMicroserviceUrl = "http://device.localhost/api/user";

    private final String deviceMicroserviceUrl = "http://localhost:8081/api/user";

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    public ResponseEntity<String> createUserInDeviceMS(UUID userId, String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.set("Content-Type", "application/json");
        Map<String, Object> requestBody = Map.of("id", userId.toString());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = deviceMicroserviceUrl + "/create";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("User was sent to the devices microservice");
                return ResponseEntity.ok("User created in devices microservice successfully");
            } else {
                logger.error("Some error appeared and user was not sent to devices microservice");
                return ResponseEntity.status(response.getStatusCode()).body("Failed to create user in devices microservice: " + response.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("Some error appeared and user was not sent to devices microservice");
            return ResponseEntity.status(ex.getStatusCode()).body("Error from devices microservice: " + ex.getResponseBodyAsString());
        }
    }

    public ResponseEntity<String> deleteUserInDevicesMS(UUID userId, String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = deviceMicroserviceUrl + "/delete/" + userId;
        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("User was deleted from the devices microservice");
                return ResponseEntity.ok("User deleted from devices microservice successfully");
            } else {
                logger.error("The user was not deleted from device microservice");
                return ResponseEntity.status(response.getStatusCode()).body("Failed to delete user in devices microservice");
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("Error deleting user from device microservice: {}", ex.getMessage(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body("Error deleting user from devices microservice: " + ex.getResponseBodyAsString());
        }
    }
}
