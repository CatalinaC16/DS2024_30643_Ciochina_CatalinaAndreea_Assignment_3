package com.example.deviceMS.controllers;

import com.example.deviceMS.dtos.deviceDTOs.DeviceDTO;
import com.example.deviceMS.services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/getAll")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        return ResponseEntity.ok(this.deviceService.getAllDevices());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.deviceService.getDeviceById(id));
    }

    @GetMapping("/getAllByUserId/{userId}")
    public ResponseEntity<List<DeviceDTO>> getAllDevicesByUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(this.deviceService.getAllDevicesByUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody DeviceDTO deviceDTO) {
        return ResponseEntity.ok(this.deviceService.createDevice(deviceDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DeviceDTO> updateDeviceById(@PathVariable("id") UUID id, @RequestBody DeviceDTO deviceDTO) {
        return ResponseEntity.ok(this.deviceService.updateDeviceById(id, deviceDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDeviceById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(this.deviceService.deleteDeviceById(id));
    }
}
