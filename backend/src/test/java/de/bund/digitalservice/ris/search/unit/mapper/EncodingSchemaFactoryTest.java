package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.mapper.EncodingSchemaFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

class EncodingSchemaFactoryTest {

  private final List<String> expectedHtmlValues =
      List.of("baseUrl/html", "baseUrl.html", "text/html", "de");
  private final List<String> expectedXmlValues =
      List.of("baseUrl/xml", "baseUrl.xml", "application/xml", "de");

  @Test()
  void generatesDocumentEncodingSchemas() {
    var expectedZipValues = List.of("baseUrl/zip", "baseUrl.zip", "application/zip", "de");
    var result = EncodingSchemaFactory.documentEncodingSchemas("baseUrl");

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
