package com.example.MCMicroService.controllers;

import com.example.MCMicroService.services.MeasurementService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/mcm/device")
@AllArgsConstructor
public class DeviceController {

    private final MeasurementService measurementService;

    @GetMapping("/{deviceId}/consumption")
    public ResponseEntity<Map<String, Object>> getEnergyConsumption(@PathVariable UUID deviceId, @RequestParam String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        Map<String, Double> hourlyConsumption = measurementService.getDailyConsumption(deviceId, selectedDate);

        List<String> hours = new ArrayList<>(hourlyConsumption.keySet());
        List<Double> values = new ArrayList<>(hourlyConsumption.values());

        Map<String, Object> response = new HashMap<>();
        response.put("hours", hours);
        response.put("values", values);

        return ResponseEntity.ok(response);
    }
}
