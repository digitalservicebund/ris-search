package de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliDocumentChange;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class EcliDocumentRepository {
  SitemapMetadataRepository jpaInterface;

  public EcliDocumentRepository(SitemapMetadataRepository jpaInterface) {
    this.jpaInterface = jpaInterface;
  }

  public List<EcliSitemapMetadata> getAllPublishedMetadataById(List<String> ids) {
    return this.jpaInterface.findAllByIsPublishedIsTrueAndIdIn(ids);
  }

  public void persistEcliDocumentsChanges(List<EcliDocumentChange> changedDocs) {
    List<EcliSitemapMetadata> toBeUpserted =
        changedDocs.stream()
            .map(
                changed -> {
                  EcliSitemapMetadata metadata = changed.metadata();
                  metadata.setPublished(
                      changed.type().equals(EcliDocumentChange.ChangeType.CHANGE));

                  return metadata;
                })
            .toList();

    this.jpaInterface.saveAll(toBeUpserted);
  }
}
