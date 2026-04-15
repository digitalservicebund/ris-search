package de.bund.digitalservice.ris.search.unit.mapper;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.mapper.NormSchemaMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.schema.LegalForceStatus;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSchema;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NormSchemaMapperTest {

  @Test
  void itMapsLegislationExpressionsAndManifestations() {
    Norm norm =
        Norm.builder()
            .id("id")
            .expressionEli("expressionEli")
            .normsDate(LocalDate.of(2025, 1, 1))
            .datePublished(LocalDate.of(2026, 1, 1))
            .tableOfContents(
                List.of(
                    TableOfContentsItem.builder()
                        .id("eId")
                        .heading("heading")
                        .marker("marker")
                        .build()))
            .articles(
                List.of(
                    Article.builder()
                        .eId("eId")
                        .name("name")
                        .text("attachmentText")
                        .manifestationEli("eli")
                        .entryIntoForceDate(LocalDate.of(2024, 1, 1))
                        .expiryDate(LocalDate.of(2025, 1, 1))
                        .build()))
            .manifestationEliExample("manifestationEli/regelungstext-1.xml")
            .workEli("workEli")
            .build();

    LegislationExpressionSchema expectedResponse =
        LegislationExpressionSchema.builder()
            .id("/v1/legislation/expressionEli")
            .legislationIdentifier("expressionEli")
            .legislationLegalForce(LegalForceStatus.IN_FORCE)
            .exampleOfWork(
                new LegislationWorkSchema(
                    "/v1/legislation/workEli",
                    "workEli",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2026, 1, 1),
                    null))
            .encoding(
                List.of(
                    LegislationObjectSchema.builder()
                        .id("/v1/legislation/manifestationEli/regelungstext-1/html")
                        .contentUrl("/v1/legislation/manifestationEli/regelungstext-1.html")
                        .encodingFormat("text/html")
                        .inLanguage("de")
                        .build(),
                    LegislationObjectSchema.builder()
                        .id("/v1/legislation/manifestationEli/regelungstext-1/xml")
                        .contentUrl("/v1/legislation/manifestationEli/regelungstext-1.xml")
                        .encodingFormat("application/xml")
                        .inLanguage("de")
                        .build(),
                    LegislationObjectSchema.builder()
                        .id("/v1/legislation/manifestationEli/zip")
                        .contentUrl("/v1/legislation/manifestationEli.zip")
                        .encodingFormat("application/zip")
                        .inLanguage("de")
                        .build()))
            .hasPart(
                List.of(
                    LegislationExpressionPartSchema.builder()
                        .id("/v1/legislation/expressionEli#eId")
                        .eId("eId")
                        .name("marker")
                        .alternativeName("heading")
                        .temporalCoverage("2024-01-01/2025-01-01")
                        .hasPart(List.of())
                        .encoding(
                            List.of(
                                LegislationObjectSchema.builder()
                                    .contentUrl(ApiConfig.Paths.LEGISLATION + "/eli")
                                    .encodingFormat("application/xml")
                                    .build()))
                        .build()))
            .build();

    Assertions.assertEquals(expectedResponse, NormSchemaMapper.fromDomain(norm));
  }
}
