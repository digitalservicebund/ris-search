package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.StandaloneArticle;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link Norm} entity. This
 * interface extends {@link ElasticsearchRepository} and focuses on operations related to {@link
 * Norm}.
 */
public interface ArticlesRepository extends ElasticsearchRepository<StandaloneArticle, String> {

  /**
   * Delete all articles that were indexed before the provided timestamp.
   *
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByIndexedAtBefore(String indexedAt);

  /**
   * Delete articles for the given workEli that were indexed before the provided timestamp.
   *
   * @param workEli the work-level ELI identifier
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByWorkEliAndIndexedAtBefore(String workEli, String indexedAt);

  /** Delete all norms that do not have an indexedAt value set. */
  void deleteByIndexedAtIsNull();
}
