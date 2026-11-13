package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.builder.NormWorkMetadataTestDataBuilder;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NormWorkMetadataMapperTest {

  @Test
  @DisplayName("Extracts vollzitat from metadaten.xml")
  void extractsVollzitat() {
    String xml =
        NormWorkMetadataTestDataBuilder.buildMetadatenXml("Verordnung zu der Vereinbarung");

    assertThat(NormWorkMetadataMapper.parseVollzitat(xml))
        .isEqualTo(Optional.of("Verordnung zu der Vereinbarung"));
  }

  @Test
  @DisplayName("Returns empty when vollzitat element is missing")
  void returnsEmptyWhenVollzitatMissing() {
    String xml = NormWorkMetadataTestDataBuilder.buildMetadatenXml(null);

    assertThat(NormWorkMetadataMapper.parseVollzitat(xml)).isEmpty();
  }

  @Test
  @DisplayName("Returns empty when vollzitat is blank")
  void returnsEmptyWhenVollzitatBlank() {
    String xml = NormWorkMetadataTestDataBuilder.buildMetadatenXml("   ");

    assertThat(NormWorkMetadataMapper.parseVollzitat(xml)).isEmpty();
  }

  @Test
  @DisplayName("Returns empty when content is not valid XML")
  void returnsEmptyWhenContentIsMalformed() {
    assertThat(NormWorkMetadataMapper.parseVollzitat("not xml")).isEmpty();
  }
}
