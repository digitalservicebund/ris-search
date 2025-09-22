package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.EncodingSchemaFactory;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EncodingSchemaFactoryTest {

  private final List<String> expectedHtmlValues =
      List.of("baseUrl/html", "baseUrl.html", "text/html", "de");
  private final List<String> expectedXmlValues =
      List.of("baseUrl/xml", "baseUrl.xml", "application/xml", "de");

  static Stream<Arguments> schemaFactories() {
    return Stream.of(
        Arguments.of(
            "caselaw", (Function<String, List<?>>) EncodingSchemaFactory::caselawEncodingSchemas),
        Arguments.of(
            "literature",
            (Function<String, List<?>>) EncodingSchemaFactory::literatureEncodingSchemas));
  }

  @ParameterizedTest(name = "generates {0} encoding schemas")
  @MethodSource("schemaFactories")
  void generatesEncodingSchemas(String name, Function<String, List<?>> factoryMethod) {
    var expectedZipValues = List.of("baseUrl/zip", "baseUrl.zip", "application/zip", "de");
    var result = factoryMethod.apply("baseUrl");

    assertThat(result).hasSize(3);
    assertThat(result.getFirst())
        .extracting("id", "contentUrl", "encodingFormat", "inLanguage")
        .containsExactlyElementsOf(expectedHtmlValues);
    assertThat(result.get(1))
        .extracting("id", "contentUrl", "encodingFormat", "inLanguage")
        .containsExactlyElementsOf(expectedXmlValues);
    assertThat(result.get(2))
        .extracting("id", "contentUrl", "encodingFormat", "inLanguage")
        .containsExactlyElementsOf(expectedZipValues);
  }

  @Test
  void generatesLegislationEncodingSchemas() {
    var expectedZipValues = List.of("zipBaseUrl/zip", "zipBaseUrl.zip", "application/zip", "de");
    var result = EncodingSchemaFactory.legislationEncodingSchemas("baseUrl", "zipBaseUrl");

    assertThat(result).hasSize(3);
    assertThat(result.getFirst())
        .extracting("id", "contentUrl", "encodingFormat", "inLanguage")
        .containsExactlyElementsOf(expectedHtmlValues);
    assertThat(result.get(1))
        .extracting("id", "contentUrl", "encodingFormat", "inLanguage")
        .containsExactlyElementsOf(expectedXmlValues);
    assertThat(result.get(2))
        .extracting("id", "contentUrl", "encodingFormat", "inLanguage")
        .containsExactlyElementsOf(expectedZipValues);
  }
}
