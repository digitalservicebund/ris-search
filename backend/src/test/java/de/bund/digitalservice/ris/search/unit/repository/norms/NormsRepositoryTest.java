package de.bund.digitalservice.ris.search.unit.repository.norms;

import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.NormsSynthesizedRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class NormsRepositoryTest {
  @Test
  void saveAll() {
    List<Norm> normList = Collections.emptyList();
    NormsSynthesizedRepository normsSynthesizedRepository =
        org.mockito.Mockito.mock(NormsSynthesizedRepository.class);
    var normsRepository = new NormsRepository(normsSynthesizedRepository);

    normsRepository.saveAll(normList);

    verify(normsSynthesizedRepository).saveAll(normList);
  }
}
