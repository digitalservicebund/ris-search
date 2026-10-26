package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository for ECLI crawler documents stored in OpenSearch.
 *
 * <p>Provides queries specific to the crawler artifacts, such as filename-based lookups and a
 * stream of published filenames.
 */
public interface EcliCrawlerDocumentRepository
    extends ElasticsearchRepository<EcliCrawlerDocument, String> {

  /** Maximum number of filenames sent to OpenSearch in a single query. */
  int FIND_BY_FILENAME_BATCH_SIZE = 1000;

  /**
   * Find crawler documents matching any of the given filenames in a single terms query on the
   * {@code filename.keyword} subfield.
   *
   * @param filenames the filenames to search for
   * @return stream of matching documents
   */
  @Query("{\"terms\": {\"filename.keyword\": ?0}}")
  Stream<EcliCrawlerDocument> findByFilenameIn(List<String> filenames);

  /**
   * Find crawler documents matching any of the given filenames. The filenames are partitioned into
   * chunks of {@value #FIND_BY_FILENAME_BATCH_SIZE}, and each chunk is queried separately.
   *
   * @param filenames the filenames to search for
   * @return stream of matching documents
   */
  default Stream<EcliCrawlerDocument> findByFilename(List<String> filenames) {
    return ListUtils.partition(filenames, FIND_BY_FILENAME_BATCH_SIZE).stream()
        .flatMap(this::findByFilenameIn);
  }

  /**
   * Stream EcliCrawlerDocuments that are considered published.
   *
   * @return stream of EcliCrawlerDocuments where isPublished is true
   */
  Stream<EcliCrawlerDocument> findByIsPublishedIsTrue();
}
