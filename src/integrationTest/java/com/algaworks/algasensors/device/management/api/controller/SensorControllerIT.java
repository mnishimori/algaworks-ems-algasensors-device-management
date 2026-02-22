package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.common.IntegrationTest;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class SensorControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SensorRepository sensorRepository;

  @AfterEach
  void cleanUp() {
    sensorRepository.deleteAll();
  }

  @Test
  void shouldReturnNoContentAndEnableSensorWhenSensorExists() throws Exception {
    var sensor = persistSensor(false);

    mockMvc.perform(put("/api/sensors/{sensorId}/enable", sensor.getId().toString()))
        .andExpect(status().isNoContent());

    var updatedSensor = sensorRepository.findById(sensor.getId());
    assertThat(updatedSensor).isPresent();
    assertThat(updatedSensor.orElseThrow().getEnabled()).isTrue();
  }

  @Test
  void shouldReturnNoContentAndDisableSensorWhenSensorExists() throws Exception {
    var sensor = persistSensor(true);

    mockMvc.perform(delete("/api/sensors/{sensorId}/enable", sensor.getId().toString()))
        .andExpect(status().isNoContent());

    var updatedSensor = sensorRepository.findById(sensor.getId());
    assertThat(updatedSensor).isPresent();
    assertThat(updatedSensor.orElseThrow().getEnabled()).isFalse();
  }

  @Test
  void shouldReturnNotFoundWhenEnableSensorAndSensorDoesNotExist() throws Exception {
    var sensorId = TSID.fast().toString();

    mockMvc.perform(put("/api/sensors/{sensorId}/enable", sensorId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFoundWhenDisableSensorAndSensorDoesNotExist() throws Exception {
    var sensorId = TSID.fast().toString();

    mockMvc.perform(delete("/api/sensors/{sensorId}/enable", sensorId))
        .andExpect(status().isNotFound());
  }

  private Sensor persistSensor(boolean enabled) {
    var sensor = Sensor.builder()
        .id(new SensorId(TSID.fast()))
        .name("Sensor")
        .ip("127.0.0.1")
        .location("Plant")
        .protocol("MQTT")
        .model("A1")
        .enabled(enabled)
        .build();
    return sensorRepository.saveAndFlush(sensor);
  }

}
