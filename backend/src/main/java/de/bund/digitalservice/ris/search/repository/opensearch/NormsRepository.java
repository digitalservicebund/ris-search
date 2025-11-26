package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link Norm} entity. This
 * interface extends {@link ElasticsearchRepository} and focuses on operations related to {@link
 * Norm}.
 */
public interface NormsRepository extends ElasticsearchRepository<Norm, String> {
  /**
   * Returns all {@link Norm} matching the given work ELI.
   *
   * @param workEli The work-level ELI identifier.
   * @return A {@link Norm}
   */
  List<Norm> getByWorkEli(String workEli);

  /**
   * Returns a {@link Norm} by its expression-level ELI.
   *
   * @param expressionEli The expression-level ELI identifier.
   * @return A {@link Norm}
   */
  Norm getByExpressionEli(String expressionEli);

  /**
   * Returns all legislation in a {@link Page}.
   *
   * @param pageable The pagination parameters.
   * @return A page of {@link Norm} entities.
   */
  @NotNull
  Page<Norm> findAll(@NotNull Pageable pageable);

  /**
   * Delete all norms that were indexed before the provided timestamp.
   *
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByIndexedAtBefore(String indexedAt);

  /**
   * Delete norms for the given workEli that were indexed before the provided timestamp.
   *
   * @param workEli the work-level ELI identifier
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByWorkEliAndIndexedAtBefore(String workEli, String indexedAt);

  /** Delete all norms that do not have an indexedAt value set. */
  void deleteByIndexedAtIsNull();
}
