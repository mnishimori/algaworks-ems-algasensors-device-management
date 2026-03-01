package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@Validated
public class SensorController {

  private final SensorRepository sensorRepository;
  private final SensorMonitoringClient sensorMonitoringClient;

  public SensorController(SensorRepository sensorRepository, SensorMonitoringClient sensorMonitoringClient) {
    this.sensorRepository = sensorRepository;
    this.sensorMonitoringClient = sensorMonitoringClient;
  }

  @GetMapping
  public Page<SensorOutput> search(@PageableDefault Pageable pageable) {
    Page<Sensor> sensors = sensorRepository.findAll(pageable);
    Page<SensorOutput> map = sensors.map(this::convertToModel);
    return map;
  }

  @GetMapping("{sensorId}")
  public SensorOutput getOne(@PathVariable @NotNull TSID sensorId) {
    Sensor sensor = findSensorById(sensorId);
    return convertToModel(sensor);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SensorOutput create(@RequestBody @Valid SensorInput input) {
    Sensor sensor = Sensor.builder()
        .id(new SensorId(IdGenerator.generateTSID()))
        .name(input.getName())
        .ip(input.getIp())
        .location(input.getLocation())
        .protocol(input.getProtocol())
        .model(input.getModel())
        .enabled(false)
        .build();

    sensor = sensorRepository.saveAndFlush(sensor);

    return convertToModel(sensor);
  }

  @PutMapping("{sensorId}")
  public SensorOutput update(@PathVariable @NotNull TSID sensorId, @RequestBody @Valid SensorInput input) {
    Sensor sensor = findSensorById(sensorId);

    sensor.setName(input.getName());
    sensor.setIp(input.getIp());
    sensor.setLocation(input.getLocation());
    sensor.setProtocol(input.getProtocol());
    sensor.setModel(input.getModel());

    sensor = sensorRepository.saveAndFlush(sensor);

    return convertToModel(sensor);
  }

  @DeleteMapping("{sensorId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable @NotNull TSID sensorId) {
    Sensor sensor = findSensorById(sensorId);

    sensorRepository.delete(sensor);
  }

  @PutMapping("{sensorId}/enable")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void enable(@PathVariable @NotNull TSID sensorId) {
    Sensor sensor = findSensorById(sensorId);
    sensor.setEnabled(true);
    sensorRepository.saveAndFlush(sensor);
    sensorMonitoringClient.enableMonitoring(sensorId);
  }

  @DeleteMapping("{sensorId}/enable")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void disable(@PathVariable @NotNull TSID sensorId) {
    Sensor sensor = findSensorById(sensorId);
    sensor.setEnabled(false);
    sensorRepository.saveAndFlush(sensor);
    sensorMonitoringClient.disableMonitoring(sensorId);
  }

  private Sensor findSensorById(TSID sensorId) {
    return sensorRepository.findById(new SensorId(sensorId))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  private SensorOutput convertToModel(Sensor sensor) {
    return SensorOutput.builder()
        .id(sensor.getId().toString())
        .name(sensor.getName())
        .ip(sensor.getIp())
        .location(sensor.getLocation())
        .protocol(sensor.getProtocol())
        .model(sensor.getModel())
        .enabled(sensor.getEnabled())
        .build();
  }
}
