package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.SharedTestConstants;
import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
import java.time.LocalDate;
import java.time.Month;
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
  void oneExpressionHasFullTimeRelevanceWindow() {
    // This test is for the prototype which won't have inkraft properly defined until 2028
    String workEli = "eli/bund/bgbl-1/1991/s101";
    String manifestationEli = workEli + "/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
    String normXml = NormTestDataBuilder.builder().eli(manifestationEli).buildNormXml();

    normsBucket.save(manifestationEli, normXml);
    indexNormsService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);
    List<Norm> expressions = normsRepository.getByWorkEliKeyword(workEli);
    assertThat(expressions).hasSize(1);
    assertThat(expressions.getFirst().getTimeRelevanceStartDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MIN);
    assertThat(expressions.getFirst().getTimeRelevanceEndDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MAX);
  }

  @Test
  @DisplayName("Three expressions cover full time relevance window")
  void threeExpressionsCoverFullTimeRelevanceWindow() {
    indexNormsService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);
    List<Norm> expressions = normsRepository.getByWorkEliKeyword("eli/bund/bgbl-1/1991/s102");
    assertThat(expressions).hasSize(3);

    expressions.sort(Comparator.comparing(Norm::getId));

    assertThat(expressions.getFirst().getTimeRelevanceStartDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MIN);
    assertThat(expressions.getFirst().getTimeRelevanceEndDate())
        .isEqualTo(LocalDate.of(1995, Month.JANUARY, 1));
    assertThat(expressions.get(1).getTimeRelevanceStartDate())
        .isEqualTo(LocalDate.of(1995, Month.JANUARY, 2));
    assertThat(expressions.get(1).getTimeRelevanceEndDate())
        .isEqualTo(LocalDate.of(2049, Month.DECEMBER, 31));
    assertThat(expressions.getLast().getTimeRelevanceStartDate())
        .isEqualTo(LocalDate.of(2050, Month.JANUARY, 1));
    assertThat(expressions.getLast().getTimeRelevanceEndDate())
        .isEqualTo(IndexNormsService.TIME_RELEVANCE_MAX);
  }

  @Test
  @DisplayName("Full citation indexes properly")
  void fullCitationIndexesProperly() {
    indexNormsService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);
    Norm expression =
        normsRepository.getByExpressionEliKeyword("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");
    assertThat(expression.getFullCitation()).startsWith("Verordnung");
  }

  @Test
  @DisplayName("Official Toc indexes properly")
  void officialTocIndexesProperly() {
    indexNormsService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);
    Norm expression =
        normsRepository.getByExpressionEliKeyword("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");
    assertThat(expression.getOfficialToc()).startsWith("Abschnitt 1");
  }

  @Test
  @DisplayName("Official foot notes index properly")
  void officialFootNotesIndexProperly() {
    indexNormsService.reindexAll(SharedTestConstants.TIMESTAMP_2024_01_01_AS_STRING);
    Norm expression =
        normsRepository.getByExpressionEliKeyword("eli/bund/bgbl-1/1991/s102/1991-01-01/1/deu");
    assertThat(expression.getOfficialFootNotes())
        .isEqualTo(
            "Authorial note in the norm title. Authorial note in an article title. Authorial note in attachment contents");
  }
}
