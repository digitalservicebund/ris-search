package de.bund.digitalservice.ris.search.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Tag("integration")
class IndexNormsServiceTest extends ContainersIntegrationBase {

  @Autowired private IndexNormsService indexNormsService;
  @Autowired private NormsRepository normsRepository;

  @Autowired private NormsBucket normsBucket;

  @BeforeEach
  void setUpSearchControllerApiTest() {
    resetBuckets();
    clearRepositoryData();
  }

  @Test
  @DisplayName("One expression has full relevance window")
  void oneExpressionHasFullRelevanceWindow() throws IOException {
    // This test is for the prototype which won't have inkraft properly defined until 2028
    String workEli = "eli/bund/bgbl-1/1991/s101";
    String normFile1 = workEli + "/1991-01-01/1/deu/1991-01-01/regelungstext-1.xml";
    normsBucket.save(normFile1, NormsTestData.simpleNormXml(normFile1, null));
    indexNormsService.reindexAll(Instant.now().toString());
    List<Norm> expressions = normsRepository.getByWorkEli(workEli);
    assertThat(expressions).hasSize(1);
    assertThat(expressions.getFirst().getRelevanceStartDate())
        .isEqualTo(IndexNormsService.RELEVANCE_MIN);
    assertThat(expressions.getFirst().getRelevanceEndDate())
        .isEqualTo(IndexNormsService.RELEVANCE_MAX);
  }

  @Test
  @DisplayName("Two expressions cover full relevance window")
  void twoExpressionsCoverFullRelevanceWindow() {
    indexNormsService.reindexAll(Instant.now().toString());
    List<Norm> expressions = normsRepository.getByWorkEli("eli/bund/bgbl-1/1991/s102");
    assertThat(expressions).hasSize(3);

    expressions.sort(Comparator.comparing(Norm::getId));

    assertThat(expressions.getFirst().getRelevanceStartDate())
        .isEqualTo(IndexNormsService.RELEVANCE_MIN);
    assertThat(expressions.getFirst().getRelevanceEndDate()).isEqualTo(LocalDate.of(1995, 1, 1));
    assertThat(expressions.get(1).getRelevanceStartDate()).isEqualTo(LocalDate.of(1995, 1, 2));
    assertThat(expressions.get(1).getRelevanceEndDate()).isEqualTo(LocalDate.of(2049, 12, 31));
    assertThat(expressions.getLast().getRelevanceStartDate()).isEqualTo(LocalDate.of(2050, 1, 1));
    assertThat(expressions.getLast().getRelevanceEndDate())
        .isEqualTo(IndexNormsService.RELEVANCE_MAX);
  }
}
