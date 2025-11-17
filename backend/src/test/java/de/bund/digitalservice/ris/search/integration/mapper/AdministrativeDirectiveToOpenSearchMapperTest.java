package de.bund.digitalservice.ris.search.integration.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdministrativeDirectiveToOpenSearchMapperTest {

  @Test
  void itMapsLdmlToAnOpenSearchEntity() {

    String ldmlString =
        LoadXmlUtils.loadXmlAsString(AdministrativeDirective.class, "KSNR0000.akn.xml");

    Instant now = Instant.now();
    AdministrativeDirective entity =
        AdministrativeDirectiveLdmlToOpenSearchMapper.map(ldmlString, now);

    AdministrativeDirective expected =
        AdministrativeDirective.builder()
            .id("KSNR0000")
            .documentNumber("KSNR0000")
            .longTitle("long title")
            .documentCategory("Verwaltungsregelung")
            .documentType("Rundschreiben")
            .content("text content")
            .normgeber("NG Ministerium")
            .entryIntoEffectDate("1978-01-17")
            .expiryDate("2026-01-01")
            .tocItems(List.of("item 1", "item 2"))
            .normReferences(List.of("PhanGB"))
            .caselawReferences(List.of("Aktivzitierung I"))
            .fundstelleReferences(List.of("Lorem Ipsum 19XX, 11"))
            .referenceNumbers(List.of("RFR I"))
            .zitierdatumItems(List.of("2024-01-01"))
            .activeAdministrativeReferences(List.of("VR Full Reference 2024-01-01 00001"))
            .activeNormReferences(List.of("ArbGG § 1 Abs 1"))
            .keywords(List.of("Schlagwort1", "Schlagwort2"))
            .fieldsOfLaw(List.of("01-01-01-01"))
            .zuordnungen(List.of("aspekt begriff"))
            .indexedAt(now.toString())
            .build();

    assertThat(entity).isEqualTo(expected);
  }
}
