package de.bund.digitalservice.ris.search.unit.sitemap.caselaw;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliSitemapMetadata;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Url;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliDocumentChange;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    EcliSitemapMetadata meta = new EcliSitemapMetadata();
    meta.setId("identifier");
    meta.setDecisionDate("2025-01-01");
    meta.setEcli("ECLI:1234");
    meta.setCourtType("BGH");
    meta.setDocumentType("decision");

    List<EcliDocumentChange> changes = new ArrayList<>();
    changes.add(new EcliDocumentChange(meta, EcliDocumentChange.ChangeType.DELETE));
    changes.add(new EcliDocumentChange(meta, EcliDocumentChange.ChangeType.CHANGE));

    Sitemap sitemap = service.createSitemaps(changes).getFirst();

    Assertions.assertEquals(
        "ECLI:1234",
        sitemap.getUrl().get(1).getDocument().getMetadata().getIsVersionOf().getValue());
    Assertions.assertEquals(
        "ECLI:1234",
        sitemap.getUrl().get(1).getDocument().getMetadata().getIsVersionOf().getValue());
  }

  @Test
  void itPartitionsSitemapsAccordingToMaxEntries() {

    List<EcliDocumentChange> changes = new ArrayList<>();
    EcliSitemapMetadata meta1 = new EcliSitemapMetadata();
    meta1.setId("DELETED1");
    EcliDocumentChange deleted1 =
        new EcliDocumentChange(meta1, EcliDocumentChange.ChangeType.DELETE);

    EcliSitemapMetadata meta2 = new EcliSitemapMetadata();
    meta2.setId("DELETED1");
    EcliDocumentChange deleted2 =
        new EcliDocumentChange(meta1, EcliDocumentChange.ChangeType.DELETE);
    changes.add(deleted1);
    changes.add(deleted2);

    List<Sitemap> sitemaps = service.createSitemaps(changes, 1);
    Assertions.assertEquals(2, sitemaps.size());
  }

  @Test
  void itReturnsSitemapFilesForAGivenDay() {
    LocalDate date = LocalDate.of(2025, 1, 1);
    when(bucket.getAllKeysByPrefix("eclicrawler/2025/01/01"))
        .thenReturn(List.of("eclicrawler/2025/01/01/sitemap_1.xml"));

    Assertions.assertEquals(
        "eclicrawler/2025/01/01/sitemap_1.xml",
        service.getSitemapFilesPathsForDay(date).getFirst());
  }

  @Test
  void itWritesARobotsTxt() {
    String expectedContent =
        """
                User-agent: DG_JUSTICE_CRAWLER
                Allow: /

                Sitemap:sitemapPath""";
    service.writeRobotsTxt(List.of("sitemapPath"));

    verify(bucket).save(SitemapService.ROBOTS_TXT_PATH, expectedContent);
  }

  @Test
  void itWritesMultipleSitemapFilesWithTheCorrectSuffix() throws JAXBException {

    List<Sitemap> sitemaps =
        List.of(new Sitemap().setUrl(List.of(new Url())), new Sitemap().setUrl(List.of(new Url())));
    LocalDate date = LocalDate.of(2025, 1, 1);

    ArgumentCaptor<String> pathArgumentCaptor = ArgumentCaptor.forClass(String.class);

    List<String> indexpath = service.writeSitemapFiles(sitemaps, date);

    verify(bucket, Mockito.times(3)).save(pathArgumentCaptor.capture(), any(String.class));

    Assertions.assertTrue(
        pathArgumentCaptor
            .getAllValues()
            .containsAll(
                List.of(
                    SitemapService.PATH_PREFIX + "2025/01/01/sitemap_1.xml",
                    SitemapService.PATH_PREFIX + "2025/01/01/sitemap_2.xml",
                    SitemapService.PATH_PREFIX + "2025/01/01/sitemap_index_1.xml")));

    Assertions.assertEquals(
        SitemapService.PATH_PREFIX + "2025/01/01/sitemap_index_1.xml", indexpath.getFirst());
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
    service.updateRobotsTxt(List.of("sitemapPath2"));

    verify(bucket).save(SitemapService.ROBOTS_TXT_PATH, expectedContent);
  }

  @Test
  void itThrowsAnErrorOnUpdatingNonExistingRobotsTxt() throws ObjectStoreServiceException {
    when(bucket.getFileAsString(SitemapService.ROBOTS_TXT_PATH)).thenReturn(Optional.empty());
    Assertions.assertThrows(
        FileNotFoundException.class,
        () -> {
          service.updateRobotsTxt(List.of("indexpath"));
        });
  }
}
