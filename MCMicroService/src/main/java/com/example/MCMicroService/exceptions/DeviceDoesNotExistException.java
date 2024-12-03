package com.example.MCMicroService.exceptions;

public class DeviceDoesNotExistException extends RuntimeException {
    public DeviceDoesNotExistException(String message) {
        super(message);
    }
}
