package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link Article} entity. This
 * interface extends {@link ElasticsearchRepository} and focuses on operations related to {@link
 * Article}.
 */
public interface ArticlesRepository extends ElasticsearchRepository<Article, String> {
  List<Article> findAllByExpressionEli(String expressionEli);

  /**
   * Delete articles for the given workEli that were indexed before the provided timestamp.
   *
   * @param workEli the work-level ELI identifier
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByWorkEliAndIndexedAtBefore(String workEli, String indexedAt);

  /** Delete all articles that do not have an indexedAt value set. */
  void deleteByIndexedAtIsNull();
}
