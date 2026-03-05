package com.algaworks.algasensors.device.management.api.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.hypersistence.tsid.TSID;
import java.io.IOException;

public class StringToTSIDDeserializer extends JsonDeserializer<TSID> {

  @Override
  public TSID deserialize(JsonParser p, DeserializationContext ctxt) {
    if (p.currentToken().isScalarValue()) {
      try {
        return TSID.from(p.getText());
      } catch (IOException e) {
        throw new RuntimeException("Invalid TSID format: " + e.getMessage());
      }
    }
    return null;
  }
}