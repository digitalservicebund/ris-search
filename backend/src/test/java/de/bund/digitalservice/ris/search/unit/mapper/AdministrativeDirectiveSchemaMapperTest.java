package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveEncodingSchema;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSchema;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdministrativeDirectiveSchemaMapperTest {

  @Test
  void itMapsFromEntityToApiSchema() {

    AdministrativeDirective entity =
        AdministrativeDirective.builder()
            .id("KN0000")
            .documentNumber("KN0000")
            .documentCategory("VV")
            .documentType("Rundschreiben")
            .referenceNumbers(List.of("RNR"))
            .entryIntoEffectDate(LocalDate.of(2024, 1, 1))
            .expiryDate(LocalDate.of(2026, 1, 1))
            .normgeber("authority")
            .fundstelleReferences(List.of("Fundstelle"))
            .normReferences(List.of("norm"))
            .tableOfContentsEntries(List.of("item 1", "item 2"))
            .datesToQuote(List.of(LocalDate.of(2024, 1, 2)))
            .build();

    AdministrativeDirectiveSchema expected =
        AdministrativeDirectiveSchema.builder()
            .id("/v1/administrative-directive/KN0000")
            .documentNumber("KN0000")
            .documentType("VV")
            .documentTypeDetail("Rundschreiben")
            .referenceNumbers(List.of("RNR"))
            .entryIntoForceDate(LocalDate.of(2024, 1, 1))
            .expiryDate(LocalDate.of(2026, 1, 1))
            .legislationAuthority("authority")
            .references(List.of("Fundstelle"))
            .citationDates(List.of(LocalDate.of(2024, 1, 2)))
            .normReferences(List.of("norm"))
            .outline(List.of("item 1", "item 2"))
            .encoding(
                List.of(
                    new AdministrativeDirectiveEncodingSchema(
                        "/v1/administrative-directive/KN0000/html",
                        "/v1/administrative-directive/KN0000.html",
                        "text/html",
                        "de"),
                    new AdministrativeDirectiveEncodingSchema(
                        "/v1/administrative-directive/KN0000/xml",
                        "/v1/administrative-directive/KN0000.xml",
                        "application/xml",
                        "de"),
                    new AdministrativeDirectiveEncodingSchema(
                        "/v1/administrative-directive/KN0000/zip",
                        "/v1/administrative-directive/KN0000.zip",
                        "application/zip",
                        "de")))
            .build();

    assertThat(expected).isEqualTo(AdministrativeDirectiveSchemaMapper.fromDomain(entity));
  }
}
