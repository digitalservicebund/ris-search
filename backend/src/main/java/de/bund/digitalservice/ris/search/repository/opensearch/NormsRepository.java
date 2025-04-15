package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Repository for interacting with the persisted norms in OpenSearchIndex */
@Component
public class NormsRepository {
  private final NormsSynthesizedRepository normsSynthesizedRepository;

  /**
   * Constructor to inject the dependencies
   *
   * @param normsSynthesizedRepository The synthesized repository to retrieve data from OpenSearch
   *     via synthesized queries
   */
  @Autowired
  public NormsRepository(NormsSynthesizedRepository normsSynthesizedRepository) {
    this.normsSynthesizedRepository = normsSynthesizedRepository;
  }

  /**
   * Returns a {@link Norm} by its work-level ELI.
   *
   * @return A {@link Norm}
   */
  public Norm getByExpressionEli(final String expressionEli) {
    return normsSynthesizedRepository.getByExpressionEli(expressionEli);
  }

  /**
   * Saves a list of {@link Norm}.
   *
   * @param norms The list of {@link Norm}.
   */
  public void saveAll(List<Norm> norms) {
    normsSynthesizedRepository.saveAll(norms);
  }
}
