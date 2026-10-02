package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.Article;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.elasticsearch.annotations.SourceFilters;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository interface for interacting with the database and managing {@link Article} entity. This
 * interface extends {@link ElasticsearchRepository} and focuses on operations related to {@link
 * Article}.
 */
public interface ArticlesRepository
    extends ElasticsearchRepository<Article, String>, ArticlesRepositoryCustom {
  List<Article> findAllByExpressionEli(String expressionEli);

  /**
   * Returns all {@link Article} revisions with the given document number. Only the expressionEli is
   * loaded from the source; all other fields are left empty.
   *
   * @param documentNumber the document number of the article revision
   * @return List of {@link Article} with only the expressionEli populated
   */
  @SourceFilters(includes = {"expressionEli"})
  List<Article> findExpressionElisByDocumentNumber(String documentNumber);

  /**
   * Delete articles for the given workEli that were indexed before the provided timestamp.
   *
   * @param workEli the work-level ELI identifier
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByWorkEliAndIndexedAtBefore(String workEli, String indexedAt);

  /**
   * Delete articles that were indexed before the provided timestamp.
   *
   * @param indexedAt ISO-8601 timestamp string cutoff
   */
  void deleteByIndexedAtBefore(String indexedAt);

  /** Delete all articles that do not have an indexedAt value set. */
  void deleteByIndexedAtIsNull();

  /**
   * Check if an article exists
   *
   * @param id the id of the article to check
   * @return true if the article exists, false otherwise
   */
  boolean existsById(@NonNull String id);
}
