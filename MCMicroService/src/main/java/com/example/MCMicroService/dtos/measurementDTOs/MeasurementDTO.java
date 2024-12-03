package com.example.MCMicroService.dtos.measurementDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDTO {
    private UUID id;
    private UUID device_id;
    private long timestamp;
    private double measure;
}
