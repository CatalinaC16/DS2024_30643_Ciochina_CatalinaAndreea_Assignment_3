package com.example.MCMicroService.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "measurements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false, updatable = false)
    private Device device;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    @Column(name = "measure", nullable = false)
    private double measure;
}
