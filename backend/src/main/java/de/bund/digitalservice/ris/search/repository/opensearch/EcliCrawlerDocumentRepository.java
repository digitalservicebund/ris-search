package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Repository for ECLI crawler documents stored in OpenSearch.
 *
 * <p>Provides queries specific to the crawler artifacts, such as filename-based lookups and a
 * stream of published filenames.
 */
public interface EcliCrawlerDocumentRepository
    extends ElasticsearchRepository<EcliCrawlerDocument, String> {

  /**
   * Find a crawler document by filename.
   *
   * @param filename the filename to search for
   * @return an Optional containing the matching document if present
   */
  Optional<EcliCrawlerDocument> findByFilenameIn(String filename);

  /**
   * Stream filenames of all published crawler documents.
   *
   * @return stream of filenames where isPublished is true
   */
  Stream<String> findFilenameByIsPublishedIsTrue();
}
