package de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.CreatedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DeletedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliDocumentChange;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class EcliDocumentRepository {
  SitemapMetadataRepository jpaInterface;

  EcliDocumentRepository(SitemapMetadataRepository jpaInterface) {
    this.jpaInterface = jpaInterface;
  }

  public List<EcliSitemapMetadata> getAllMetadataById(Iterable<String> ids) {
    return this.jpaInterface.findAllById(ids);
  }

  public void persistEcliDocumentsChanges(List<EcliDocumentChange> changedDocs) {
    List<EcliSitemapMetadata> toBeUpserted = new ArrayList<>();
    List<EcliSitemapMetadata> toBeDeleted = new ArrayList<>();

    changedDocs.forEach(
        changed -> {
          switch (changed) {
            case CreatedDocument created -> toBeUpserted.add(created.metadata());
            case DeletedDocument deleted -> toBeDeleted.add(deleted.metadata());
          }
        });

    this.jpaInterface.saveAll(toBeUpserted);
    this.jpaInterface.deleteAll(toBeDeleted);
  }
}
