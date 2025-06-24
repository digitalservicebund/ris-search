package de.bund.digitalservice.ris.search.unit.mapper;

import de.bund.digitalservice.ris.search.mapper.NormResponseMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.schema.LegalForceStatus;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionPartSchema;
import de.bund.digitalservice.ris.search.schema.LegislationExpressionSchema;
import de.bund.digitalservice.ris.search.schema.LegislationObjectSchema;
import de.bund.digitalservice.ris.search.schema.LegislationWorkSchema;
import de.bund.digitalservice.ris.search.schema.TableOfContentsSchema;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NormResponseMapperTest {

  @Test
  void itMapsATableOfContents() {
    Norm norm =
        Norm.builder()
            .tableOfContents(
                List.of(
                    TableOfContentsItem.builder()
                        .id("id1")
                        .marker("marker1")
                        .heading("heading1")
                        .children(
                            List.of(
                                TableOfContentsItem.builder()
                                    .id("subId1")
                                    .marker("subMarker1")
                                    .heading("subHeading1")
                                    .build()))
                        .build(),
                    TableOfContentsItem.builder()
                        .id("id2")
                        .marker("marker2")
                        .heading("heading2")
                        .build()))
            .build();

    List<TableOfContentsSchema> expected =
        List.of(
            new TableOfContentsSchema(
                "id1",
                "marker1",
                "heading1",
                List.of(
                    new TableOfContentsSchema("subId1", "subMarker1", "subHeading1", List.of()))),
            new TableOfContentsSchema("id2", "marker2", "heading2", List.of()));

    List<TableOfContentsSchema> actual =
        Objects.requireNonNull(NormResponseMapper.fromDomain(norm).workExample()).tableOfContents();
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void itMapsLegislationExpressionsAndManifestations() {
    Norm norm =
        Norm.builder()
            .id("id")
            .expressionEli("expressionEli")
            .articles(
                List.of(
                    Article.builder()
                        .eId("eId")
                        .text("articleText")
                        .manifestationEli("eli")
                        .build()))
            .manifestationEliExample("manifestationEli/regelungstext-1.xml")
            .workEli("workEli")
            .build();

    LegislationWorkSchema expectedResponse =
        LegislationWorkSchema.builder()
            .id("/v1/legislation/workEli")
            .legislationIdentifier("workEli")
            .workExample(
                LegislationExpressionSchema.builder()
                    .id("/v1/legislation/expressionEli")
                    .legislationIdentifier("expressionEli")
                    .legislationLegalForce(LegalForceStatus.IN_FORCE)
                    .tableOfContents(List.of())
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
                                .isActive(true)
                                .encoding(
                                    List.of(
                                        LegislationObjectSchema.builder()
                                            .contentUrl("eli")
                                            .encodingFormat("application/xml")
                                            .build()))
                                .build()))
                    .build())
            .build();

    Assertions.assertEquals(expectedResponse, NormResponseMapper.fromDomain(norm));
  }
}
