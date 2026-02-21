package com.algaworks.algasensors.device.management.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Sensor {
    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id", columnDefinition = "BIGINT", nullable = false))
    private SensorId id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private String protocol;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private Boolean enabled;
}
