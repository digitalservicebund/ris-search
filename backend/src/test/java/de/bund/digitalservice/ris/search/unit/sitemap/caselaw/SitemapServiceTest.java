package de.bund.digitalservice.ris.search.unit.sitemap.caselaw;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.ChangedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.CreatedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DeletedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.SitemapService;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  @Test
  void itWritesARobotsTxt() {
    String expectedContent =
        """
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /
                Sitemap:sitemapPath""";
    service.writeRobotsTxt("sitemapPath");

    verify(bucket).save(SitemapService.ROBOTS_TXT_PATH, expectedContent);
  }

  @Test
  void itUpdatesAnExistingRobotsTxt() throws FileNotFoundException, ObjectStoreServiceException {
    String existingContent =
        """
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /
                Sitemap:sitemapPath1""";
    String expectedContent =
        """
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /
                Sitemap:sitemapPath1
                Sitemap:sitemapPath2""";
    when(bucket.getFileAsString("eclicrawler/robots.txt")).thenReturn(Optional.of(existingContent));
    service.updateRobotsTxt("sitemapPath2");

    verify(bucket).save(SitemapService.ROBOTS_TXT_PATH, expectedContent);
  }

  @Test
  void itThrowsAnErrorOnUpdatingNonExistingRobotsTxt() throws ObjectStoreServiceException {
    when(bucket.getFileAsString(SitemapService.ROBOTS_TXT_PATH)).thenReturn(Optional.empty());
    Assertions.assertThrows(
        FileNotFoundException.class,
        () -> {
          service.updateRobotsTxt("indexpath");
        });
  }
}
