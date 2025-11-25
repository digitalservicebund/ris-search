package de.bund.digitalservice.ris.search.integration.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.LoadXmlUtils;
import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.time.Instant;
import java.time.LocalDate;
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
            .headline("administrative directive headline")
            .documentType("VR")
            .documentTypeDetail("Rundschreiben")
            .shortReport("administrative directive test short report")
            .legislationAuthority("NG Ministerium")
            .tableOfContentsEntries(List.of("item 1", "item 2"))
            .entryIntoEffectDate(LocalDate.of(1978, 1, 17))
            .expiryDate(LocalDate.of(2026, 1, 1))
            .normReferences(List.of("PhanGB"))
            .caselawReferences(List.of("Aktivzitierung I"))
            .references(List.of("Lorem Ipsum 19XX, 11"))
            .referenceNumbers(List.of("RFR I"))
            .citationDates(List.of(LocalDate.of(2024, 1, 1)))
            .activeAdministrativeReferences(List.of("VR Full Reference 2024-01-01 00001"))
            .activeNormReferences(List.of("ArbGG § 1 Abs 1"))
            .keywords(List.of("Schlagwort1", "Schlagwort2"))
            .fieldsOfLaw(List.of("01-01-01-01"))
            .indexedAt(now.toString())
            .build();

    assertThat(entity).isEqualTo(expected);
  }
}
