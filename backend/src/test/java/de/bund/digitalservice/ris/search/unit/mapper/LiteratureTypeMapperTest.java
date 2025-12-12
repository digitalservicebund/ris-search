package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.bund.digitalservice.ris.search.mapper.LiteratureSchemaMapper;
import de.bund.digitalservice.ris.search.mapper.LiteratureSearchSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LiteratureTypeMapperTest {

  @ParameterizedTest
  @CsvSource({"XXLU00001,uli", "XXLS000001,sli"})
  void itDeterminesTheDocumentTypeBasedOnDocumentNumber(String docNumber, String expected) {
    Literature literature = Literature.builder().id(docNumber).documentNumber(docNumber).build();
    assertThat(LiteratureSchemaMapper.fromDomain(literature).literatureType()).isEqualTo(expected);
    assertThat(LiteratureSearchSchemaMapper.fromDomain(literature).literatureType())
        .isEqualTo(expected);
  }

  @Test
  void itThrowsAnIllegalStateExceptionIfDocumentNumberIsInvalid() {
    Literature literature = Literature.builder().id("XXAB0000").documentNumber("XXAB0000").build();
    assertThrows(
        IllegalStateException.class,
        () -> {
          LiteratureSchemaMapper.fromDomain(literature);
        });
  }
}
