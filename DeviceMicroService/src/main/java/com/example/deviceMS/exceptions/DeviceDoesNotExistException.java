package com.example.deviceMS.exceptions;

public class DeviceDoesNotExistException extends RuntimeException {
    public DeviceDoesNotExistException(String message) {
        super(message);
    }
}
