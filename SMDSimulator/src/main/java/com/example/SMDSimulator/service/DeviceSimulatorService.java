package com.example.SMDSimulator.service;

import com.example.SMDSimulator.processor.CsvProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceSimulatorService {

    @Autowired
    private CsvProcessor csvProcessor;

    public void startSimulation(String deviceId) {
        csvProcessor.readAndSendData(deviceId);
    }
}
