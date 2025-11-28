package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import static de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants.DATE_2_1;

import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Test data for AdministrativeDirective documents. */
public class AdministrativeDirectiveTestData {

  public static final List<AdministrativeDirective> allDocuments = new ArrayList<>();

  static {
    allDocuments.add(
        AdministrativeDirective.builder()
            .id("/v1/administrative_directive/KN0000")
            .documentNumber("KSNR0000")
            .headline("Test long title")
            .documentType("VR")
            .documentTypeDetail("Rundschreiben")
            .shortReport("text content")
            .legislationAuthority("NG Ministerium")
            .entryIntoEffectDate(LocalDate.of(1978, 1, 17))
            .expiryDate(LocalDate.of(2026, 1, 1))
            .normReferences(List.of("PhanGB"))
            .caselawReferences(List.of("Aktivzitierung I"))
            .references(List.of("Lorem Ipsum 19XX, 11"))
            .referenceNumbers(List.of("RFR I"))
            .citationDates(List.of(DATE_2_1))
            .activeAdministrativeReferences(List.of("VR Full Reference 2024-01-01 00001"))
            .activeNormReferences(List.of("ArbGG § 1 Abs 1"))
            .keywords(List.of("Schlagwort1", "Schlagwort2"))
            .fieldsOfLaw(List.of("01-01-01-01"))
            .indexedAt(Instant.now().toString())
            .build());
  }
}
