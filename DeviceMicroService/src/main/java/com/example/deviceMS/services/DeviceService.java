package com.example.deviceMS.services;

import com.example.deviceMS.dtos.deviceDTOs.DeviceDTO;
import com.example.deviceMS.dtos.mappers.DeviceMapper;
import com.example.deviceMS.entities.Device;
import com.example.deviceMS.entities.User;
import com.example.deviceMS.exceptions.DeviceDoesNotExistException;
import com.example.deviceMS.exceptions.InvalidDataException;
import com.example.deviceMS.exceptions.UserDoesNotExistException;
import com.example.deviceMS.producer.DeviceProducer;
import com.example.deviceMS.repositories.DeviceRepository;
import com.example.deviceMS.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final UserRepository userRepository;

    private final DeviceMapper deviceMapper;

    private final DeviceProducer deviceProducer;

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    public List<DeviceDTO> getAllDevices() {
        List<Device> devices = this.deviceRepository.findAll();
        List<DeviceDTO> deviceDTOS = new ArrayList<>();
        if (!devices.isEmpty())
            deviceDTOS = devices.stream().map(deviceMapper::convertToDTO).collect(Collectors.toList());
        logger.info("The list of devices was requested");
        return deviceDTOS;
    }

    public DeviceDTO getDeviceById(UUID id) throws DeviceDoesNotExistException {
        Optional<Device> deviceOptional = this.deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            logger.error("Device with id={} was not found", id);
            throw new DeviceDoesNotExistException("The requested device does not exist");
        }
        logger.info("Device with id={} was retrieved", id);
        Device device = deviceOptional.get();
        return deviceMapper.convertToDTO(device);
    }

    public List<DeviceDTO> getAllDevicesByUser(UUID id) throws UserDoesNotExistException {
        List<Device> devices = this.deviceRepository.findAll();
        devices = devices.stream()
                .filter(device -> device.getUser().getId() != null && device.getUser().getId().equals(id))
                .collect(Collectors.toList());

        List<DeviceDTO> deviceDTOS = new ArrayList<>();
        if (!devices.isEmpty())
            deviceDTOS = devices.stream().map(deviceMapper::convertToDTO).collect(Collectors.toList());
        logger.info("The list of devices by user was requested");
        return deviceDTOS;
    }

    @Transactional
    public String deleteDeviceById(UUID id) throws DeviceDoesNotExistException {
        Optional<Device> deviceOptional = this.deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            logger.error("Device with id={} was not found", id);
            throw new DeviceDoesNotExistException("The requested device does not exist");
        }
        Device device = deviceOptional.get();
        this.deviceRepository.delete(device);
        deviceProducer.sendDeviceMessage(device, "delete");
        logger.info("Device with id={} was deleted", id);
        return "Device with id= " + id + " was deleted successfully!";
    }

    public DeviceDTO createDevice(DeviceDTO deviceDTO) {
        Device device = this.deviceMapper.convertToEntity(deviceDTO);
        device = this.deviceRepository.save(device);
        logger.info("The device was created");
        deviceProducer.sendDeviceMessage(device, "insert");
        return this.deviceMapper.convertToDTO(device);
    }

    @Transactional
    public DeviceDTO updateDeviceById(UUID id, DeviceDTO deviceDTO) throws DeviceDoesNotExistException {
        Optional<Device> deviceOptional = this.deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            logger.error("Device with id={} was not found", id);
            throw new DeviceDoesNotExistException("The requested device does not exist");
        }
        Device device = deviceOptional.get();
        device = this.updateDeviceValues(device, deviceDTO);
        this.deviceRepository.save(device);
        deviceProducer.sendDeviceMessage(device, "update");
        logger.info("The device with id={} was updated", id);
        return this.deviceMapper.convertToDTO(device);
    }

    public Device updateDeviceValues(Device existingDevice, DeviceDTO deviceDTO) {

        if (deviceDTO.getAddress() != null && !deviceDTO.getAddress().isEmpty()) {
            existingDevice.setAddress(deviceDTO.getAddress());
        }

        if (deviceDTO.getDescription() != null && !deviceDTO.getDescription().isEmpty()) {
            existingDevice.setDescription(deviceDTO.getDescription());
        }

        if (deviceDTO.getMaxHourlyEnergyConsumption() < 0) {
            throw new InvalidDataException("Max hourly energy consumption must be greater than 0");
        } else if (deviceDTO.getMaxHourlyEnergyConsumption() == 0) {
        } else {
            existingDevice.setMaxHourlyEnergyConsumption(deviceDTO.getMaxHourlyEnergyConsumption());
        }
        if (deviceDTO.getUser_id() != null) {
            Optional<User> userOptional = this.userRepository.findById(deviceDTO.getUser_id());
            if (userOptional.isEmpty()) {
                throw new UserDoesNotExistException("User with ID " + deviceDTO.getUser_id() + " was not found");
            }
            User user = userOptional.get();
            existingDevice.setUser(user);
        }
        return existingDevice;
    }
}
