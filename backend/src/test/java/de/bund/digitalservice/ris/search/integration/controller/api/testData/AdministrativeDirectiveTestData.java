package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdministrativeDirectiveTestData {

  public static final List<AdministrativeDirective> allDocuments = new ArrayList<>();

  static {
    allDocuments.add(
        AdministrativeDirective.builder()
            .id("/v1/administrative_directive/KN0000")
            .documentNumber("KSNR0000")
            .longTitle("long title")
            .documentCategory("VR")
            .documentType("Rundschreiben")
            .content("text content")
            .normgeber("NG Ministerium")
            .entryIntoEffectDate(LocalDate.of(1978, 1, 17))
            .expiryDate(LocalDate.of(2026, 1, 1))
            .normReferences(List.of("PhanGB"))
            .caselawReferences(List.of("Aktivzitierung I"))
            .fundstelleReferences(List.of("Lorem Ipsum 19XX, 11"))
            .referenceNumbers(List.of("RFR I"))
            .datesToQuote(List.of(LocalDate.of(2024, 1, 1)))
            .activeAdministrativeReferences(List.of("VR Full Reference 2024-01-01 00001"))
            .activeNormReferences(List.of("ArbGG § 1 Abs 1"))
            .keywords(List.of("Schlagwort1", "Schlagwort2"))
            .fieldsOfLaw(List.of("01-01-01-01"))
            .zuordnungen(List.of("aspekt begriff"))
            .indexedAt(Instant.now().toString())
            .build());
  }
}
