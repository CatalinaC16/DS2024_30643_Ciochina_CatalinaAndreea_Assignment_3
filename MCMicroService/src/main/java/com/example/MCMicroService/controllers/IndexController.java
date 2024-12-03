package com.example.MCMicroService.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class IndexController {

    @GetMapping(value = "/get")
    public ResponseEntity<String> getStatus() {
        return new ResponseEntity<>("Monitoring microservice is running...", HttpStatus.OK);
    }
}