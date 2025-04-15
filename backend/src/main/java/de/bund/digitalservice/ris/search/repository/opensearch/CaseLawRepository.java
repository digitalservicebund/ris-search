package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Repository for interacting with the persisted caselaw documentation units in OpenSearchIndex */
@Component
public class CaseLawRepository {
  private final CaseLawSynthesizedRepository caseLawSynthesizedRepository;

  /**
   * Constructor to inject the dependencies
   *
   * @param caseLawSynthesizedRepository The synthesized repository to retrieve data from OpenSearch
   *     via synthesized queries
   */
  @Autowired
  public CaseLawRepository(CaseLawSynthesizedRepository caseLawSynthesizedRepository) {
    this.caseLawSynthesizedRepository = caseLawSynthesizedRepository;
  }

  /**
   * Saves a list of {@link CaseLawDocumentationUnit}.
   *
   * @param caseLaws The list of {@link CaseLawDocumentationUnit}.
   */
  public void saveAll(List<CaseLawDocumentationUnit> caseLaws) {
    caseLawSynthesizedRepository.saveAll(caseLaws);
  }

  public List<CaseLawDocumentationUnit> getByDocumentNumber(String documentNumber) {
    return caseLawSynthesizedRepository.findByDocumentNumber(documentNumber);
  }
}
