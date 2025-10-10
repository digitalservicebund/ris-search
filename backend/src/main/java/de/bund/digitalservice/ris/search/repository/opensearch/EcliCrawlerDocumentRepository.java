package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EcliCrawlerDocumentRepository
    extends ElasticsearchRepository<EcliCrawlerDocument, String> {
  Optional<EcliCrawlerDocument> findByFilenameIn(String filename);

  Stream<String> findFilenameByIsPublishedIsTrue();
}
