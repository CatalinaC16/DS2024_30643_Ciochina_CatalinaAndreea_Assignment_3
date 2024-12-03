package com.example.MCMicroService.services;

import com.example.MCMicroService.dtos.deviceDTOs.DeviceDTO;
import com.example.MCMicroService.dtos.mappers.DeviceMapper;
import com.example.MCMicroService.entities.Device;
import com.example.MCMicroService.entities.Measurement;
import com.example.MCMicroService.exceptions.DeviceDoesNotExistException;
import com.example.MCMicroService.exceptions.InvalidDataException;
import com.example.MCMicroService.repositories.DeviceRepository;
import com.example.MCMicroService.repositories.MeasurementRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DeviceService {

    private final DeviceMapper deviceMapper;

    private final DeviceRepository deviceRepository;

    private final MeasurementRepository measurementRepository;

    public DeviceDTO createDevice(DeviceDTO deviceDTO) {
        Device device = this.deviceMapper.convertToEntity(deviceDTO);
        device = this.deviceRepository.save(device);
        return this.deviceMapper.convertToDTO(device);
    }

    @Transactional
    public DeviceDTO updateDevice(DeviceDTO deviceDTO) throws DeviceDoesNotExistException {
        Optional<Device> deviceOptional = this.deviceRepository.findById(deviceDTO.getId());
        if (deviceOptional.isEmpty()) {
            throw new DeviceDoesNotExistException("The requested device does not exist");
        }
        Device device = deviceOptional.get();
        device = this.updateDeviceValues(device, deviceDTO);
        this.deviceRepository.save(device);
        return this.deviceMapper.convertToDTO(device);
    }

    public Device updateDeviceValues(Device existingDevice, DeviceDTO deviceDTO) {

        if (deviceDTO.getMaxHourlyEnergyConsumption() < 0) {
            throw new InvalidDataException("Max hourly energy consumption must be greater than 0");
        } else if (deviceDTO.getMaxHourlyEnergyConsumption() == 0) {
        } else {
            existingDevice.setMaxHourlyEnergyConsumption(deviceDTO.getMaxHourlyEnergyConsumption());
        }
        if (deviceDTO.getUser_id() != null) {
            existingDevice.setUser_id(deviceDTO.getUser_id());
        }
        return existingDevice;
    }

    @Transactional
    public String deleteDevice(UUID id) throws DeviceDoesNotExistException {
        Optional<Device> deviceOptional = this.deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            throw new DeviceDoesNotExistException("The requested device does not exist");
        }
        Device device = deviceOptional.get();

        List<Measurement> measurementList = this.measurementRepository.findAllByDeviceId(id);
        if (!measurementList.isEmpty()) {
            this.measurementRepository.deleteAll(measurementList);
        }
        this.deviceRepository.delete(device);
        return "Device with id= " + id + " was deleted successfully!";
    }
}
