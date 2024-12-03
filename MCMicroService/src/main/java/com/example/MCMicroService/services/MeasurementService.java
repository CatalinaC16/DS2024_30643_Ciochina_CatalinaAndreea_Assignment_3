package com.example.MCMicroService.services;

import com.example.MCMicroService.dtos.deviceDTOs.DeviceDTO;
import com.example.MCMicroService.dtos.mappers.DeviceMapper;
import com.example.MCMicroService.dtos.mappers.MeasurementMapper;
import com.example.MCMicroService.dtos.measurementDTOs.MeasurementDTO;
import com.example.MCMicroService.entities.Device;
import com.example.MCMicroService.entities.Measurement;
import com.example.MCMicroService.repositories.DeviceRepository;
import com.example.MCMicroService.repositories.MeasurementRepository;
import com.example.MCMicroService.webSockets.WebSocketSender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    private final DeviceRepository deviceRepository;

    private final MeasurementMapper measurementMapper;

    private final DeviceMapper deviceMapper;

    private final WebSocketSender webSocketSender;

    @Transactional
    public void createMeasurement(MeasurementDTO measurementDTO) {
        Optional<Device> deviceOptional = this.deviceRepository.findById(measurementDTO.getDevice_id());
        if (deviceOptional.isEmpty()) {
            DeviceDTO deviceDTO = new DeviceDTO();
            deviceDTO.setId(measurementDTO.getDevice_id());
            Device device = this.deviceMapper.convertToEntity(deviceDTO);
            this.deviceRepository.save(device);
        }
        Measurement measurement = this.measurementMapper.convertToEntity(measurementDTO);
        measurementRepository.save(measurement);
        System.out.println("Measurement saved: " + measurement);
    }

    public double checkHourlyConsumption(UUID deviceId, long timestamp, boolean check) {
        Optional<Device> optionalDevice = this.deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            System.out.println("** Device not found for ID: " + deviceId + " **");
            return 0;
        }
        Device targetDevice = optionalDevice.get();

        LocalDateTime targetHour = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<Measurement> measurementsForTargetHour = this.measurementRepository.findAllByDeviceId(deviceId).stream()
                .filter(m -> {
                    LocalDateTime measurementHour = LocalDateTime.ofInstant(Instant.ofEpochMilli(m.getTimestamp()), ZoneOffset.UTC)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0);
                    return measurementHour.equals(targetHour);
                }).collect(Collectors.toList());

        if (measurementsForTargetHour.isEmpty()) {
            System.out.println("** No measurements for the target hour. **");
            return 0;
        }

        double averageConsumption = measurementsForTargetHour.stream()
                .mapToDouble(Measurement::getMeasure)
                .average()
                .orElse(0);

        if (averageConsumption > targetDevice.getMaxHourlyEnergyConsumption()) {
            System.out.println("** Warning: The average consumption for the hour (" + averageConsumption + ") exceeds the maximum limit (" + targetDevice.getMaxHourlyEnergyConsumption() + ").**");
            if (check)
                this.webSocketSender.sendAlert(deviceId.toString(), averageConsumption, targetDevice.getMaxHourlyEnergyConsumption(), targetDevice.getUser_id().toString());
        } else {
            System.out.println("** The average consumption for the hour is within limits. **");
        }
        return averageConsumption;
    }

    public Map<String, Double> getDailyConsumption(UUID deviceId, LocalDate date) {
        Map<String, Double> hourlyConsumption = new LinkedHashMap<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime hourStart = date.atStartOfDay().plusHours(hour);

            long timestampStartOfHour = hourStart.toInstant(ZoneOffset.UTC).toEpochMilli();

            double hourlyMeasure = this.checkHourlyConsumption(deviceId, timestampStartOfHour, false);

            //System.out.println("Time "+timestampStartOfHour+ " Measure: "+hourlyMeasure);

            String hourLabel = String.format("%02d:00", hour+2);
            hourlyConsumption.put(hourLabel, hourlyMeasure);
        }
        return hourlyConsumption;
    }
}
