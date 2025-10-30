package de.bund.digitalservice.ris.search.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class LiteratureRepositoryIntegrationTest extends ContainersIntegrationBase {

  @Autowired private LiteratureRepository literatureRepository;

  private final Literature literature = mockLiterature("XXLU000000001", "2025-04-02T00:00:00Z");

  @BeforeEach
  void setUp() {
    literatureRepository.deleteAll();
    literatureRepository.save(literature);
  }

  @Test
  void deleteByIndexedAtBeforeDeletesOnlyEntriesOnOrBeforeIndexedAtDate() {
    assertThat(literatureRepository.findAll()).hasSize(1);
    literatureRepository.deleteByIndexedAtBefore("2025-04-01T00:00:00Z");
    assertThat(literatureRepository.findAll()).hasSize(1);
    literatureRepository.deleteByIndexedAtBefore("2025-04-02T00:00:00Z");
    assertThat(literatureRepository.findAll()).isEmpty();
  }

  @Test
  void deleteByIndexedAtNullDeletesOnlyEntriesWithNoIndexedAtDate() {
    literatureRepository.save(mockLiterature("XXLU000000002", null));
    assertThat(literatureRepository.findAll()).hasSize(2);
    literatureRepository.deleteByIndexedAtIsNull();
    assertThat(literatureRepository.findAll()).hasSize(1).containsExactly(literature);
  }

  @Test
  void deleteAllByIdDeletesOnlyEntriesWhichMatchOneOfTheIds() {
    literatureRepository.save(mockLiterature("XXLU000000002", null));
    literatureRepository.save(mockLiterature("XXLU000000003", null));
    assertThat(literatureRepository.findAll()).hasSize(3);
    literatureRepository.deleteAllById(List.of("XXLU000000002", "XXLU000000003"));
    assertThat(literatureRepository.findAll()).hasSize(1).containsExactly(literature);
  }

  private static Literature mockLiterature(String docNumber, String indexedAt) {
    return Literature.builder()
        .id(docNumber)
        .documentNumber(docNumber)
        .indexedAt(indexedAt)
        .build();
  }
}
