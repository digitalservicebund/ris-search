package de.bund.digitalservice.ris.search.repository;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class NormsRepositoryIntegrationTest extends ContainersIntegrationBase {

  private static final String ELI_1 = "eli/bund/bgbl-1/2020/s1126/2025-05-05/1/deu";
  private static final String ELI_2 = "eli/bund/bgbl-1/2020/s1126/2026-05-05/1/deu";
  private static final String ELI_3 = "eli/bund/bgbl-1/2020/s2222/2025-05-05/1/deu";

  @BeforeEach
  void setUp() {
    normsRepository.deleteAll();
    normsRepository.saveAll(List.of(mockNorm(ELI_1), mockNorm(ELI_2), mockNorm(ELI_3)));
  }

  @Test
  void findAllByIdInReturnsOnlyNormsMatchingOneOfTheIds() {
    Page<Norm> actual =
        normsRepository.findAllByIdIn(List.of(ELI_1, ELI_3, "unknown"), Pageable.unpaged());

    assertThat(actual.getTotalElements()).isEqualTo(2);
    assertThat(actual).extracting(Norm::getId).containsExactlyInAnyOrder(ELI_1, ELI_3);
  }

  @Test
  void findAllByIdInRespectsPagination() {
    Page<Norm> actual =
        normsRepository.findAllByIdIn(List.of(ELI_1, ELI_2, ELI_3), Pageable.ofSize(2));

    assertThat(actual.getContent()).hasSize(2);
    assertThat(actual.getTotalElements()).isEqualTo(3);
    assertThat(actual.getTotalPages()).isEqualTo(2);
  }

  @Test
  void findAllByIdInReturnsEmptyPageWhenNoIdMatches() {
    Page<Norm> actual = normsRepository.findAllByIdIn(List.of("unknown"), Pageable.unpaged());

    assertThat(actual).isEmpty();
  }

  private static Norm mockNorm(String expressionEli) {
    return Norm.builder().id(expressionEli).expressionEli(expressionEli).build();
  }
}
