package com.algaworks.algasensors.device.management.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

  @Bean
  public SensorMonitoringClient sensorMonitoringClient(RestClientFactory restClientFactory) {
    RestClient restClient = restClientFactory.temperatureMonitoringRestClient();
    RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
    return httpServiceProxyFactory.createClient(SensorMonitoringClient.class);
  }
}
