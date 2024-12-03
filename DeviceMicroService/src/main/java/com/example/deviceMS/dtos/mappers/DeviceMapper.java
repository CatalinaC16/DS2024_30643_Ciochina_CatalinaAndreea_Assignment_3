package com.example.deviceMS.dtos.mappers;

import com.example.deviceMS.dtos.deviceDTOs.DeviceDTO;
import com.example.deviceMS.entities.Device;
import com.example.deviceMS.entities.User;
import com.example.deviceMS.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeviceMapper implements Mapper<Device, DeviceDTO> {

    private final UserRepository userRepository;

    @Override
    public DeviceDTO convertToDTO(Device device) {
        if (device != null) {
            return DeviceDTO.builder()
                    .id(device.getId())
                    .description(device.getDescription())
                    .address(device.getAddress())
                    .maxHourlyEnergyConsumption(device.getMaxHourlyEnergyConsumption())
                    .user_id(device.getUser().getId())
                    .build();
        }
        return null;
    }

    @Override
    public Device convertToEntity(DeviceDTO deviceDTO) {
        if (deviceDTO != null) {
            Optional<User> userOptional = null;
            if (!deviceDTO.getUser_id().equals(null))
                userOptional = this.userRepository.findById(deviceDTO.getUser_id());
            User user = new User();
            if (userOptional.isPresent())
                user = userOptional.get();
            return Device.builder()
                    .address(deviceDTO.getAddress())
                    .description(deviceDTO.getDescription())
                    .user(user)
                    .maxHourlyEnergyConsumption(deviceDTO.getMaxHourlyEnergyConsumption())
                    .build();
        }
        return null;
    }
}
