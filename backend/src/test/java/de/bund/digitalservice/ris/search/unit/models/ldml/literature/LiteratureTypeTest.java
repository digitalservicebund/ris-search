package de.bund.digitalservice.ris.search.unit.models.ldml.literature;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureType;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LiteratureTypeTest {

  static Stream<Arguments> possibleLiteratureTypes() {
    return Stream.of(
        Arguments.of("XXLS00001", LiteratureType.SLI),
        Arguments.of("XXLU0001", LiteratureType.ULI),
        Arguments.of("ABCD0000", LiteratureType.UNKNOWN));
  }

  @ParameterizedTest()
  @MethodSource("possibleLiteratureTypes")
  void itReturnsTheCorrectTypeBasedOnDocumentNumber(
      String documentNumber, LiteratureType expectedType) {
    assertThat(LiteratureType.getByDocumentNumber(documentNumber)).isEqualTo(expectedType);
  }
}
