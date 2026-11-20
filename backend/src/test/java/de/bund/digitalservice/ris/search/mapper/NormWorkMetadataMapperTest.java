package de.bund.digitalservice.ris.search.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.builder.NormWorkMetadataTestDataBuilder;
import de.bund.digitalservice.ris.search.models.ldml.norm.NormWorkMetadata;
import de.bund.digitalservice.ris.search.models.ldml.norm.StandDerDokumentarischenBearbeitung;
import de.bund.digitalservice.ris.search.models.ldml.norm.Standangabe;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NormWorkMetadataMapperTest {

  @Test
  @DisplayName(
      "Parses vollzitat, standangabe and standDerDokumentarischenBearbeitung from metadaten.xml")
  void parsesCompleteWorkMetadata() {
    String xml =
        NormWorkMetadataTestDataBuilder.buildMetadatenXml(
            "Verordnung zu der Vereinbarung",
            List.of(
                new Standangabe("Zuletzt geändert durch Testanpassungen", "stand"),
                new Standangabe("Neugefasst durch Testdaten", "neufassung")),
            List.of(new StandDerDokumentarischenBearbeitung("Hinweis Testdaten")));

    NormWorkMetadata metadata = NormWorkMetadataMapper.parse(xml).orElseThrow();

    assertThat(metadata.getVollzitat()).isEqualTo("Verordnung zu der Vereinbarung");
    assertThat(metadata.getStandangabe())
        .extracting(Standangabe::getText)
        .containsExactly("Zuletzt geändert durch Testanpassungen", "Neugefasst durch Testdaten");
    assertThat(metadata.getStandDerDokumentarischenBearbeitung())
        .extracting(StandDerDokumentarischenBearbeitung::getText)
        .containsExactly("Hinweis Testdaten");
  }

  @Test
  @DisplayName("Parses metadaten.xml without any optional elements present")
  void parsesWorkMetadataWithoutOptionalElements() {
    String xml = NormWorkMetadataTestDataBuilder.buildMetadatenXml(null);

    NormWorkMetadata metadata = NormWorkMetadataMapper.parse(xml).orElseThrow();

    assertThat(metadata.getVollzitat()).isNull();
    assertThat(metadata.getStandangabe()).isEmpty();
    assertThat(metadata.getStandDerDokumentarischenBearbeitung()).isEmpty();
  }

  @Test
  @DisplayName("Returns empty when content is not valid XML")
  void returnsEmptyWhenContentIsMalformed() {
    assertThat(NormWorkMetadataMapper.parse("not xml")).isEmpty();
  }
}
