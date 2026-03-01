package com.algaworks.algasensors.device.management.api.client;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

  private final RestClient.Builder builder;

  public RestClient temperatureMonitoringRestClient() {
    return builder
        .baseUrl("http://localhost:8082")
        .requestFactory(generateClientHttpRequestFactory())
        .defaultStatusHandler(HttpStatusCode::isError, (status, headers) -> {
          throw new SensorMonitoringClientBadGatewayException();
        })
        .build();
  }

  private ClientHttpRequestFactory generateClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    simpleClientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(5));
    simpleClientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(3));
    return simpleClientHttpRequestFactory;
  }

}
