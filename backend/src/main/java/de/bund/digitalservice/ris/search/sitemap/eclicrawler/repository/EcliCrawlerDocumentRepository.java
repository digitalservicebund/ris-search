package de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcliCrawlerDocumentRepository extends JpaRepository<EcliCrawlerDocument, String> {
  List<EcliCrawlerDocument> findAllByIsPublishedIsTrueAndIdIn(List<String> ids);
}
