package com.userMS.UserMicroService.services;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${device.microservice.url}")
    private String deviceMicroserviceUrl;

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    public ResponseEntity<String> createUserInDeviceMS(UUID userId, String jwtToken) {
        logger.info("Starting createUserInDeviceMS with userId: {} and token: {}", userId, jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.set("Content-Type", "application/json");
        Map<String, Object> requestBody = Map.of("id", userId.toString());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String url = deviceMicroserviceUrl + "/create";

        logger.debug("Prepared request for URL: {} with body: {}", url, requestBody);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            logger.debug("Received response with status: {} and body: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("User was sent to the devices microservice successfully.");
                return ResponseEntity.ok("User created in devices microservice successfully");
            } else {
                logger.error("Failed to create user in devices microservice. Response: {}", response.getBody());
                return ResponseEntity.status(response.getStatusCode()).body("Failed to create user in devices microservice: " + response.getBody());
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("HTTP error while creating user in devices microservice: {}", ex.getMessage(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body("Error from devices microservice: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error while creating user in devices microservice: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }


    public ResponseEntity<String> deleteUserInDevicesMS(UUID userId, String jwtToken) {
        logger.info("Starting deleteUserInDevicesMS with userId: {} and token: {}", userId, jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = deviceMicroserviceUrl + "/delete/" + userId;

        logger.debug("Prepared DELETE request for URL: {}", url);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
            logger.debug("Received response with status: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("User was deleted from the devices microservice successfully.");
                return ResponseEntity.ok("User deleted from devices microservice successfully");
            } else {
                logger.error("Failed to delete user in devices microservice. Status: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body("Failed to delete user in devices microservice");
            }
        } catch (HttpStatusCodeException ex) {
            logger.error("HTTP error while deleting user from devices microservice: {}", ex.getMessage(), ex);
            return ResponseEntity.status(ex.getStatusCode()).body("Error deleting user from devices microservice: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error while deleting user from devices microservice: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

}
