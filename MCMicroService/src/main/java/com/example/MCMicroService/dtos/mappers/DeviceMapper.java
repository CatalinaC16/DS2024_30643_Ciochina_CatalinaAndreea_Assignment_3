package com.example.MCMicroService.dtos.mappers;


import com.example.MCMicroService.dtos.deviceDTOs.DeviceDTO;
import com.example.MCMicroService.entities.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeviceMapper implements Mapper<Device, DeviceDTO> {

    @Override
    public DeviceDTO convertToDTO(Device device) {
        if (device != null) {
            return DeviceDTO.builder()
                    .id(device.getId())
                    .user_id(device.getUser_id())
                    .maxHourlyEnergyConsumption(device.getMaxHourlyEnergyConsumption())
                    .build();
        }
        return null;
    }

    @Override
    public Device convertToEntity(DeviceDTO deviceDTO) {
        if (deviceDTO != null) {
            return Device.builder()
                    .id(deviceDTO.getId())
                    .maxHourlyEnergyConsumption(deviceDTO.getMaxHourlyEnergyConsumption())
                    .user_id(deviceDTO.getUser_id())
                    .build();
        }
        return null;
    }
}
