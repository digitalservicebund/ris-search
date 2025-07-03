package de.bund.digitalservice.ris.search.unit.sitemap.caselaw;

import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.ChangedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.CreatedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DeletedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.SitemapService;
import jakarta.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SitemapServiceTest {

  SitemapService service;
  @Mock PortalBucket bucket;

  @BeforeEach
  void setup() throws JAXBException {
    service = new SitemapService(bucket);
  }

  @Test
  void itCreatesUrlSetsForCreatedAndDeletedFiles() {

    String identifier = "identifier";
    LocalDate decisionDate = LocalDate.of(2025, 1, 1);
    String ecli = "ECLI:1234";
    String courtType = "BGH";
    String documentType = "docType";

    List<ChangedDocument> changes = new ArrayList<>();
    changes.add(new DeletedDocument("DELETED_IDENTIFIER"));
    changes.add(
        new CreatedDocument(
            CaseLawDocumentationUnit.builder()
                .id(identifier)
                .decisionDate(decisionDate)
                .ecli(ecli)
                .courtType(courtType)
                .documentType(documentType)
                .build()));

    Sitemap sitemap = service.createSitemaps(changes).getFirst();

    Assertions.assertEquals("deleted", sitemap.getUrl().getFirst().getDocument().getStatus());
    Assertions.assertEquals(
        ecli, sitemap.getUrl().get(1).getDocument().getMetadata().getIsVersionOf().getValue());
  }

  @Test
  void itIgnoresIncompleteCaseLawDocumentationUnits() {

    String identifier = "identifier";

    List<ChangedDocument> changes = new ArrayList<>();
    changes.add(new CreatedDocument(CaseLawDocumentationUnit.builder().id(identifier).build()));

    Assertions.assertTrue(service.createSitemaps(changes).isEmpty());
  }

  @Test
  void itPartitionsSitemapsAccordingToMaxEntries() {

    List<ChangedDocument> changes = new ArrayList<>();
    changes.add(new DeletedDocument("DELETED_1"));
    changes.add(new DeletedDocument("DELETED_2"));

    List<Sitemap> sitemaps = service.createSitemaps(changes, 1);
    Assertions.assertEquals(2, sitemaps.size());
  }

  @Test
  void itReturnsSitemapFilesForAGivenDay() {
    LocalDate date = LocalDate.of(2025, 1, 1);
    when(bucket.getAllKeysByPrefix("eclicrawler/2025/1/1"))
        .thenReturn(List.of("eclicrawler/2025/1/1/sitemap_1.xml"));

    Assertions.assertEquals(
        "eclicrawler/2025/1/1/sitemap_1.xml", service.getSitemapFilesPathsForDay(date).getFirst());
  }
}
