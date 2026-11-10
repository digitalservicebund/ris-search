package de.bund.digitalservice.ris.search.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class RichtungTest {

  @Test
  void serializesToItsLowercaseValue() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    assertThat(objectMapper.writeValueAsString(Richtung.AKTIV)).isEqualTo("\"aktiv\"");
    assertThat(objectMapper.writeValueAsString(Richtung.PASSIV)).isEqualTo("\"passiv\"");
  }

  @Test
  void fromLdmlMatchesCaseInsensitively() {
    assertThat(Richtung.fromLdml("aktiv")).isEqualTo(Richtung.AKTIV);
    assertThat(Richtung.fromLdml("PASSIV")).isEqualTo(Richtung.PASSIV);
  }

  @Test
  void fromLdmlReturnsNullForUnknownValuesAndNull() {
    assertThat(Richtung.fromLdml("unbekannt")).isNull();
    assertThat(Richtung.fromLdml(null)).isNull();
  }
}
