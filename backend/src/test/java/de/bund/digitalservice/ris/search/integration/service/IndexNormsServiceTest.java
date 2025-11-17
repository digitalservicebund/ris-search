package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class IndexNormsServiceTest extends ContainersIntegrationBase {

  @Autowired private IndexNormsService indexNormsService;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    resetBuckets();
    clearRepositoryData();
  }

  @Test
  @DisplayName("One expression has full time relevance window")
  void oneExpressionHasFullTimeRelevanceWindow() throws IOException {
    // This test is for the prototype which won't have inkraft properly defined until 2028
    String workEli = "eli/bund/bgbl-1/1991/s101";
    String normFile1 = workEli + "/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
    normsBucket.save(normFile1, NormsTestData.simpleNormXml(normFile1, null));
    indexNormsService.reindexAll(Instant.now().toString());
    List<Norm> expressions = normsRepository.getByWorkEli(workEli);
    assertThat(expressions).hasSize(1);
    assertThat(expressions.getFirst().getTimeRelevanceStartDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MIN);
    assertThat(expressions.getFirst().getTimeRelevanceEndDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MAX);
  }

  @Test
  @DisplayName("Three expressions cover full time relevance window")
  void threeExpressionsCoverFullTimeRelevanceWindow() {
    indexNormsService.reindexAll(Instant.now().toString());
    List<Norm> expressions = normsRepository.getByWorkEli("eli/bund/bgbl-1/1991/s102");
    assertThat(expressions).hasSize(3);

    expressions.sort(Comparator.comparing(Norm::getId));

    assertThat(expressions.getFirst().getTimeRelevanceStartDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MIN);
    assertThat(expressions.getFirst().getTimeRelevanceEndDate())
        .isEqualTo(LocalDate.of(1995, 1, 1));
    assertThat(expressions.get(1).getTimeRelevanceStartDate()).isEqualTo(LocalDate.of(1995, 1, 2));
    assertThat(expressions.get(1).getTimeRelevanceEndDate()).isEqualTo(LocalDate.of(2049, 12, 31));
    assertThat(expressions.getLast().getTimeRelevanceStartDate())
        .isEqualTo(LocalDate.of(2050, 1, 1));
    assertThat(expressions.getLast().getTimeRelevanceEndDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MAX);
  }

  @Test
  @DisplayName("Full citation indexes properly")
  void fullCitationIndexesProperly() {
    indexNormsService.reindexAll(Instant.now().toString());
    Norm expression =
        normsRepository.getByExpressionEli("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");
    assertThat(expression.getFullCitation()).startsWith("Verordnung");
  }

  @Test
  @DisplayName("Official Toc indexes properly")
  void officialTocIndexesProperly() {
    indexNormsService.reindexAll(Instant.now().toString());
    Norm expression =
        normsRepository.getByExpressionEli("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");
    assertThat(expression.getOfficialToc()).startsWith("Abschnitt 1");
  }

  @Test
  @DisplayName("Official foot notes index properly")
  void officialFootNotesIndexProperly() {
    indexNormsService.reindexAll(Instant.now().toString());
    Norm expression =
        normsRepository.getByExpressionEli("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");
    assertThat(expression.getOfficialFootNotes())
        .isEqualTo(
            "Authorial note in the norm title. Authorial note in an article title. Authorial note in attachment contents");
  }
}
