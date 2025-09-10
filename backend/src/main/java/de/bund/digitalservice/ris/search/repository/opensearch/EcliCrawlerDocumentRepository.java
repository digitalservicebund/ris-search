package de.bund.digitalservice.ris.search.repository.opensearch;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocumentOS;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EcliCrawlerDocumentRepository
    extends ElasticsearchRepository<EcliCrawlerDocumentOS, String> {
  List<EcliCrawlerDocumentOS> findAllByIsPublishedIsTrueAndIdIn(List<String> ids);

  List<EcliCrawlerDocumentOS> findAllByIsPublishedIsTrue();

  List<EcliCrawlerDocumentOS> findAllByIsPublishedIsTrueAndIdNotIn(List<String> ids);
}
