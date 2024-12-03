package com.example.MCMicroService.dtos.deviceDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {
    private UUID id;
    private int maxHourlyEnergyConsumption;
    private UUID user_id;
}
