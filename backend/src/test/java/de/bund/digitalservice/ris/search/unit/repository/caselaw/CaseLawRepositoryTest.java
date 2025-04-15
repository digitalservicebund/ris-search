package de.bund.digitalservice.ris.search.unit.repository.caselaw;

import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawSynthesizedRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CaseLawRepositoryTest {
  @Test
  void saveAll() {
    List<CaseLawDocumentationUnit> documentationUnitList = Collections.emptyList();
    CaseLawSynthesizedRepository caseLawSynthesizedRepository =
        Mockito.mock(CaseLawSynthesizedRepository.class);

    var caseLawRepository = new CaseLawRepository(caseLawSynthesizedRepository);

    caseLawRepository.saveAll(documentationUnitList);

    verify(caseLawSynthesizedRepository).saveAll(documentationUnitList);
  }
}
