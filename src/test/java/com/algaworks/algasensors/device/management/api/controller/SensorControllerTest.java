package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorControllerTest {

  @Mock
  private SensorRepository sensorRepository;

  @InjectMocks
  private SensorController sensorController;

  @Test
  void shouldEnableSensorWhenSensorExists() {
    var sensorId = TSID.fast();
    var sensor = buildSensor(sensorId, false);
    when(sensorRepository.findById(any(SensorId.class))).thenReturn(Optional.of(sensor));

    sensorController.enable(sensorId);

    assertThat(sensor.getEnabled()).isTrue();
    verify(sensorRepository).saveAndFlush(sensor);
  }

  @Test
  void shouldThrowNotFoundWhenEnableSensorAndSensorDoesNotExist() {
    var sensorId = TSID.fast();
    when(sensorRepository.findById(any(SensorId.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> sensorController.enable(sensorId))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void shouldDisableSensorWhenSensorExists() {
    var sensorId = TSID.fast();
    var sensor = buildSensor(sensorId, true);
    when(sensorRepository.findById(any(SensorId.class))).thenReturn(Optional.of(sensor));

    sensorController.disable(sensorId);

    assertThat(sensor.getEnabled()).isFalse();
    verify(sensorRepository).saveAndFlush(sensor);
  }

  @Test
  void shouldThrowNotFoundWhenDisableSensorAndSensorDoesNotExist() {
    var sensorId = TSID.fast();
    when(sensorRepository.findById(any(SensorId.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> sensorController.disable(sensorId))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  private Sensor buildSensor(TSID sensorId, boolean enabled) {
    return Sensor.builder()
        .id(new SensorId(sensorId))
        .name("Sensor")
        .ip("127.0.0.1")
        .location("Plant")
        .protocol("MQTT")
        .model("A1")
        .enabled(enabled)
        .build();
  }

}
