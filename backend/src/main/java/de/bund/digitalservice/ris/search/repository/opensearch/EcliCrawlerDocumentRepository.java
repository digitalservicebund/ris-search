package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EcliCrawlerDocumentRepository
    extends ElasticsearchRepository<EcliCrawlerDocument, String> {
  List<EcliCrawlerDocument> findAllByFilenameIn(Iterable<String> ids);

  Stream<EcliCrawlerDocument> findAllByIsPublishedIsTrue();
}
