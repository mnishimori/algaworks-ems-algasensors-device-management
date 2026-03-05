package com.algaworks.algasensors.device.management.api.model;

import io.hypersistence.tsid.TSID;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorMonitoringOutput {

  private TSID id;
  private Double lastTemperature;
  private OffsetDateTime updatedAt;
  private Boolean enabled;
}