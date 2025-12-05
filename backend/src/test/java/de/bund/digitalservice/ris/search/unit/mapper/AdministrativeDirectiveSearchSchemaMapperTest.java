package de.bund.digitalservice.ris.search.unit.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveSearchSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.schema.AdministrativeDirectiveSearchSchema;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdministrativeDirectiveSearchSchemaMapperTest {

  @Test
  void itMapsFromEntityToSearchSchemaSchema() {

    AdministrativeDirective entity =
        AdministrativeDirective.builder()
            .id("KN0000")
            .documentNumber("KN0000")
            .headline("headline")
            .documentType("VV")
            .shortReport("shortReport")
            .referenceNumbers(List.of("RNR"))
            .legislationAuthority("legislationAuthority")
            .entryIntoEffectDate(LocalDate.of(2024, 1, 1))
            .build();

    AdministrativeDirectiveSearchSchema expected =
        AdministrativeDirectiveSearchSchema.builder()
            .id("/v1/administrative-directive/KN0000")
            .documentNumber("KN0000")
            .headline("headline")
            .shortReport("shortReport")
            .documentType("VV")
            .referenceNumbers(List.of("RNR"))
            .legislationAuthority("legislationAuthority")
            .entryIntoForceDate(LocalDate.of(2024, 1, 1))
            .build();

    assertThat(expected).isEqualTo(AdministrativeDirectiveSearchSchemaMapper.fromDomain(entity));
  }
}
