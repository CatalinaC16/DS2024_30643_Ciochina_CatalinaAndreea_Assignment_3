package com.example.MCMicroService.repositories;

import com.example.MCMicroService.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    List<Measurement> findAllByDeviceId(UUID id);

    @Query("SELECT m FROM Measurement m WHERE m.device.id = :deviceId AND m.timestamp BETWEEN :start AND :end")
    List<Measurement> findMeasurementsByDeviceAndDateRange(@Param("deviceId") UUID deviceId,
                                                           @Param("start") long start,
                                                           @Param("end") long end);
}
