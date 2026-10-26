package de.bund.digitalservice.ris.search.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class DatumsTypTest {

  @Test
  void serializesToItsDisplayValue() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();

    assertThat(objectMapper.writeValueAsString(DatumsTyp.ENTSCHEIDUNGSDATUM))
        .isEqualTo("\"Entscheidungsdatum\"");
    assertThat(objectMapper.writeValueAsString(DatumsTyp.MITTEILUNGSDATUM))
        .isEqualTo("\"Mitteilungsdatum\"");
    assertThat(
            objectMapper.writeValueAsString(DatumsTyp.DATUM_DER_ZUSTELLUNG_AN_VERKUENDUNGS_STATT))
        .isEqualTo("\"Datum der Zustellung an Verkündungs statt\"");
  }

  @Test
  void fromLdmlNameReturnsNullForNull() {
    assertThat(DatumsTyp.fromLdmlName(null)).isNull();
  }
}
