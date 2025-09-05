package de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SitemapMetadataRepository extends JpaRepository<EcliSitemapMetadata, String> {
  List<EcliSitemapMetadata> findAllByIsPublishedIsTrueAndIdIn(List<String> ids);
}
