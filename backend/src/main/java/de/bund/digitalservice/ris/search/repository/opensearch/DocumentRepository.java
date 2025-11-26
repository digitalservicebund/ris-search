package de.bund.digitalservice.ris.search.repository.opensearch;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Generic repository interface for document entities stored in OpenSearch/Elasticsearch.
 *
 * <p>This interface extends Spring Data's ElasticsearchRepository and adds common document
 * maintenance queries used by the application (e.g. deletion by indexed timestamp).
 *
 * @param <T> the document entity type
 */
@NoRepositoryBean
public interface DocumentRepository<T> extends ElasticsearchRepository<T, String> {

  /**
   * Delete all documents that were indexed before the provided timestamp.
   *
   * @param indexedAt ISO-8601 timestamp string; documents with indexedAt before this value will be
   *     removed
   */
  void deleteByIndexedAtBefore(String indexedAt);

  /** Delete all documents that do not have an indexedAt value set. */
  void deleteByIndexedAtIsNull();

  /**
   * Delete all documents with the given ids.
   *
   * @param ids iterable of document ids to delete
   */
  void deleteAllById(@NotNull Iterable<? extends String> ids);

  /**
   * Find documents by their document number.
   *
   * @param documentNumber the document number to search for
   * @return list of matching documents; may be empty
   */
  List<T> findByDocumentNumber(String documentNumber);
}
