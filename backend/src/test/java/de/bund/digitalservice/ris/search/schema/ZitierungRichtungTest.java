package de.bund.digitalservice.ris.search.schema;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ZitierungRichtungTest {

  @Test
  void serializesToItsLowercaseValue() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    assertThat(objectMapper.writeValueAsString(ZitierungRichtung.AKTIV)).isEqualTo("\"aktiv\"");
    assertThat(objectMapper.writeValueAsString(ZitierungRichtung.PASSIV)).isEqualTo("\"passiv\"");
  }
}
