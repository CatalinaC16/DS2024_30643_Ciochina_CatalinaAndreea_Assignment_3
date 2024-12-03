package com.example.MCMicroService.dtos.mappers;

import com.example.MCMicroService.dtos.measurementDTOs.MeasurementDTO;
import com.example.MCMicroService.entities.Device;
import com.example.MCMicroService.entities.Measurement;
import com.example.MCMicroService.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MeasurementMapper implements Mapper<Measurement, MeasurementDTO> {

    private final DeviceRepository deviceRepository;

    @Override
    public MeasurementDTO convertToDTO(Measurement measurement) {
        if (measurement != null) {
            return MeasurementDTO.builder()
                    .id(measurement.getId())
                    .device_id(measurement.getDevice().getId())
                    .timestamp(measurement.getTimestamp())
                    .measure(measurement.getMeasure())
                    .build();
        }
        return null;
    }

    @Override
    public Measurement convertToEntity(MeasurementDTO measurementDTO) {

        if (measurementDTO != null) {
            Optional<Device> deviceOptional = null;
            if (!measurementDTO.getDevice_id().equals(null))
                deviceOptional = this.deviceRepository.findById(measurementDTO.getDevice_id());
            Device device = new Device();
            if (deviceOptional.isPresent())
                device = deviceOptional.get();
            return Measurement.builder()
                    .device(device)
                    .timestamp(measurementDTO.getTimestamp())
                    .measure(measurementDTO.getMeasure())
                    .build();
        }
        return null;
    }
}

