package de.bund.digitalservice.ris.search.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.bund.digitalservice.ris.search.api.schema.CaseLawSchema;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.SharedTestConstants;
import de.bund.digitalservice.ris.search.mapper.CaseLawSchemaMapper;
import de.bund.digitalservice.ris.search.models.PublicationStatus;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaseLawSchemaMapperTest {

  @Test
  @DisplayName("Correctly maps attributes")
  void fromDomainSingle() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var source =
        CaseLawDocumentationUnit.builder()
            .id("id1")
            .documentNumber("BFRE000087655")
            .ecli("ECLI:DE:FGNI:1975:0526.IXL180.73.0A")
            .courtType("KG")
            .location("Berlin")
            .documentType("Urteil")
            .decisionDate(SharedTestConstants.DATE_2_2)
            .fileNumbers(List.of("FileNumberTest"))
            .dissentingOpinion("eine abweichende Meinung")
            .decisionGrounds("diese Entscheidungsgründe")
            .headnote("Orientierungssatz")
            .headline("Test")
            .otherHeadnote("Sonstiger Orientierungssatz")
            .otherLongText("Long text")
            .caseFacts("Tatbestand")
            .outline("outlineTest")
            .judicialBody("judicial body")
            .courtKeyword("KG Berlin")
            .keywords(List.of("one", "two"))
            .decisionName(List.of("decisionName"))
            .deviatingDocumentNumber(List.of("deviatingDocumentNumber"))
            .publicationStatus(PublicationStatus.PUBLISHED.toString())
            .documentationOffice("DS")
            .error(false)
            .legalEffect("JA")
            .build();

    var ignoredOrChangedFields =
        Set.of(
            "documentationOffice",
            "error",
            "id",
            "indexedAt",
            "articles",
            "legalEffect",
            "procedures",
            "publicationStatus",
            "courtKeyword");

    // When
    CaseLawSchema destination = CaseLawSchemaMapper.fromDomain(source);

    // Then
    // compare the values
    Set<String> equalFieldNames =
        Arrays.stream(CaseLawDocumentationUnit.class.getDeclaredFields())
            .map(Field::getName)
            .filter(field -> !ignoredOrChangedFields.contains(field))
            .collect(Collectors.toSet());

    for (String fieldName : equalFieldNames) {
      Field fieldInSource = CaseLawDocumentationUnit.class.getDeclaredField(fieldName);
      fieldInSource.setAccessible(true);

      Field fieldInDestination = CaseLawSchema.class.getDeclaredField(fieldName);
      fieldInDestination.setAccessible(true);

      Object valueInSource = fieldInSource.get(source);
      Object valueInDestination = fieldInDestination.get(destination);

      assertEquals(valueInSource, valueInDestination, "Field " + fieldName + " should be equal");
    }

    // compare changed names
    assertEquals(source.courtKeyword(), destination.courtName());
  }
}
