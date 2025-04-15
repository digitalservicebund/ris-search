package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link Norm} entity. This
 * interface extends {@link ElasticsearchRepository} and focuses on operations related to {@link
 * Norm}.
 */
public interface NormsSynthesizedRepository extends ElasticsearchRepository<Norm, String> {
  /**
   * Returns a {@link Norm} by its work-level ELI.
   *
   * @return A {@link Norm}
   */
  Norm getByWorkEli(String workEli);

  /**
   * Returns a {@link Norm} by its expression-level ELI.
   *
   * @return A {@link Norm}
   */
  Norm getByExpressionEli(String expressionEli);

  /** Returns all legislation in a {@link Page}. */
  @NotNull
  Page<Norm> findAll(@NotNull Pageable pageable);

  void deleteByIndexedAtBefore(String indexedAt);
}
