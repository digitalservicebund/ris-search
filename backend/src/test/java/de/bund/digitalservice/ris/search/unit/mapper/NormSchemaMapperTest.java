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
                        .headline("heading")
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

  @Test
  void itMapsTheHasPartTreeStructure() {
    Norm norm =
        Norm.builder()
            .id("id")
            .expressionEli("expressionEli")
            .normsDate(LocalDate.of(2025, 1, 1))
            .datePublished(LocalDate.of(2026, 1, 1))
            .tableOfContents(
                List.of(
                    TableOfContentsItem.builder()
                        .id("book")
                        .heading("bookHeading")
                        .marker("book 1")
                        .children(
                            List.of(
                                TableOfContentsItem.builder()
                                    .id("chapter")
                                    .heading("chapterHeading")
                                    .marker("chapter 1")
                                    .children(
                                        List.of(
                                            TableOfContentsItem.builder()
                                                .id("articleEid1")
                                                .marker("article 1")
                                                .heading("articleHeading 1")
                                                .build(),
                                            TableOfContentsItem.builder()
                                                .id("articleEid2")
                                                .marker("article 2")
                                                .heading("articleHeading 2")
                                                .build()))
                                    .build(),
                                TableOfContentsItem.builder()
                                    .id("attachments")
                                    .heading("attachmentsHeading")
                                    .marker("attachments 1")
                                    .children(
                                        List.of(
                                            TableOfContentsItem.builder()
                                                .id("attachmentEid")
                                                .marker("attachment 1")
                                                .heading("attachment")
                                                .build()))
                                    .build()))
                        .build()))
            .articles(
                List.of(
                    Article.builder()
                        .eId("articleEid1")
                        .name("articleName1")
                        .entryIntoForceDate(LocalDate.of(2024, 1, 1))
                        .expiryDate(LocalDate.of(2025, 1, 1))
                        .build(),
                    Article.builder()
                        .eId("articleEid2")
                        .name("articleName2")
                        .entryIntoForceDate(LocalDate.of(2024, 1, 1))
                        .expiryDate(LocalDate.of(2025, 1, 1))
                        .build(),
                    Article.builder()
                        .eId("attachmentEid")
                        .name("attachmentName")
                        .entryIntoForceDate(LocalDate.of(2024, 1, 1))
                        .expiryDate(LocalDate.of(2025, 1, 1))
                        .manifestationEli("attachment/eli")
                        .build()))
            .manifestationEliExample("manifestationEli/regelungstext-1.xml")
            .workEli("workEli")
            .build();

    var expectedParts =
        List.of(
            LegislationExpressionPartSchema.builder()
                .id("/v1/legislation/expressionEli#book")
                .eId("book")
                .name("book 1")
                .headline("bookHeading")
                .temporalCoverage("")
                .encoding(List.of())
                .hasPart(
                    List.of(
                        LegislationExpressionPartSchema.builder()
                            .id("/v1/legislation/expressionEli#chapter")
                            .eId("chapter")
                            .name("chapter 1")
                            .headline("chapterHeading")
                            .temporalCoverage("")
                            .encoding(List.of())
                            .hasPart(
                                List.of(
                                    LegislationExpressionPartSchema.builder()
                                        .id("/v1/legislation/expressionEli#articleEid1")
                                        .eId("articleEid1")
                                        .name("article 1")
                                        .headline("articleHeading 1")
                                        .temporalCoverage("2024-01-01/2025-01-01")
                                        .encoding(List.of())
                                        .hasPart(List.of())
                                        .build(),
                                    LegislationExpressionPartSchema.builder()
                                        .id("/v1/legislation/expressionEli#articleEid2")
                                        .eId("articleEid2")
                                        .name("article 2")
                                        .headline("articleHeading 2")
                                        .temporalCoverage("2024-01-01/2025-01-01")
                                        .encoding(List.of())
                                        .hasPart(List.of())
                                        .build()))
                            .build(),
                        LegislationExpressionPartSchema.builder()
                            .id("/v1/legislation/expressionEli#attachments")
                            .eId("attachments")
                            .name("attachments 1")
                            .headline("attachmentsHeading")
                            .temporalCoverage("")
                            .encoding(List.of())
                            .hasPart(
                                List.of(
                                    LegislationExpressionPartSchema.builder()
                                        .id("/v1/legislation/expressionEli#attachmentEid")
                                        .eId("attachmentEid")
                                        .name("attachment 1")
                                        .headline("attachment")
                                        .temporalCoverage("2024-01-01/2025-01-01")
                                        .hasPart(List.of())
                                        .encoding(
                                            List.of(
                                                LegislationObjectSchema.builder()
                                                    .contentUrl("/v1/legislation/attachment/eli")
                                                    .encodingFormat("application/xml")
                                                    .build()))
                                        .build()))
                            .build()))
                .build());

    Assertions.assertEquals(expectedParts, NormSchemaMapper.fromDomain(norm).hasPart());
  }
}
