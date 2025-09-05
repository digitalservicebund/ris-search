package de.bund.digitalservice.ris.search.unit.sitemap.caselaw.repository;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliDocumentRepository;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliSitemapMetadata;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.SitemapMetadataRepository;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliDocumentChange;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliDocumentRepositoryTest {

  @Mock SitemapMetadataRepository jpaRepository;

  EcliDocumentRepository repository;

  @BeforeEach
  void setup() {
    this.repository = new EcliDocumentRepository(jpaRepository);
  }

  @Test
  void itRetrievesAllMetadataOfPublishedSitemaps() {
    repository.getAllPublishedMetadataById(List.of("id1", "id2"));
    verify(jpaRepository)
        .findAllByIsPublishedIsTrueAndIdIn(
            argThat(list -> list.getFirst().equals("id1") && list.getLast().equals("id2")));
  }

  @Test
  void itPersistsAllEcliDocumentChanges() {
    EcliSitemapMetadata toBeDeletedMetadata = new EcliSitemapMetadata();
    toBeDeletedMetadata.setId("id");
    toBeDeletedMetadata.setEcli("ecli");
    toBeDeletedMetadata.setDocumentType("doctype");
    toBeDeletedMetadata.setCourtType("courtType");
    toBeDeletedMetadata.setDecisionDate("2025-01-01");

    EcliSitemapMetadata toBePublished = new EcliSitemapMetadata();
    toBeDeletedMetadata.setId("id");
    toBeDeletedMetadata.setEcli("ecli");
    toBeDeletedMetadata.setDocumentType("doctype");
    toBeDeletedMetadata.setCourtType("courtType");
    toBeDeletedMetadata.setDecisionDate("2025-01-01");

    EcliDocumentChange publish =
        new EcliDocumentChange(toBePublished, EcliDocumentChange.ChangeType.CHANGE);
    EcliDocumentChange delete =
        new EcliDocumentChange(toBeDeletedMetadata, EcliDocumentChange.ChangeType.DELETE);
    repository.persistEcliDocumentsChanges(List.of(publish, delete));

    verify(jpaRepository)
        .saveAll(
            argThat(
                (iterable -> {
                  List<EcliSitemapMetadata> list = new ArrayList<>();
                  iterable.iterator().forEachRemaining(list::add);

                  return list.getFirst().isPublished() && !list.getLast().isPublished();
                })));
  }
}
