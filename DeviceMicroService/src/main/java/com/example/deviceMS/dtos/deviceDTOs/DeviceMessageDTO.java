package com.example.deviceMS.dtos.deviceDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMessageDTO {
    private UUID id;
    private String action;
    private int maxHourlyEnergyConsumption;
    private UUID user_id;
}
