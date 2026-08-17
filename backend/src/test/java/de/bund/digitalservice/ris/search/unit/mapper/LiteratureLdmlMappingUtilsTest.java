package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlMappingUtils;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LiteratureLdmlMappingUtilsTest {

  private static Stream<Arguments> provideYearsOfPublication() {
    return Stream.of(
        Arguments.of(" 2020 ", LocalDate.of(2020, Month.JANUARY, 1)),
        Arguments.of("2020", LocalDate.of(2020, Month.JANUARY, 1)),
        Arguments.of(" 2020-05 ", LocalDate.of(2020, Month.MAY, 1)),
        Arguments.of("2020-05", LocalDate.of(2020, Month.MAY, 1)),
        Arguments.of(" 2020-05-23 ", LocalDate.of(2020, Month.MAY, 23)),
        Arguments.of("2020-05-23", LocalDate.of(2020, Month.MAY, 23)),
        Arguments.of("XX", null),
        Arguments.of("1986 - 1987", null),
        Arguments.of("2001 (vermutlich)", null),
        Arguments.of("2003, 127-133 (Schriften des Vereins für Socialpolitik", null));
  }

  @ParameterizedTest()
  @MethodSource("provideYearsOfPublication")
  void firstPublicationDateIsNullOnInvalidDate(String yearValue, LocalDate expectedDate) {

    assertThat(LiteratureLdmlMappingUtils.extractFirstYearOfPublication(List.of(yearValue)))
        .isEqualTo(expectedDate);
  }
}
