package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob.LAST_PROCESSED_CHANGELOG;
import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob.PATH_PREFIX;
import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob.ROBOTS_TXT_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemapindex.Sitemapindex;
import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliCrawlerDocumentService;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapService;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliSitemapJobTest {

  EcliSitemapJob sitemapJob;
  @Mock EcliSitemapService sitemapService;
  @Mock CaseLawIndexSyncJob syncJob;
  @Mock PortalBucket portalBucket;
  @Mock CaseLawBucket caseLawBucket;
  @Mock EcliCrawlerDocumentService documentService;

  @BeforeEach
  void setup() {
    sitemapJob =
        new EcliSitemapJob(
            sitemapService, portalBucket, caseLawBucket, syncJob, documentService, "frontend/url/");
  }

  @Test
  void itReturnsSuccessfulIfItRanAlready() {
    LocalDate day = LocalDate.now();
    when(sitemapService.getSitemapFilesPathsForDay(PATH_PREFIX, day))
        .thenReturn(List.of("2025/01/01"));

    Job.ReturnCode code = sitemapJob.runJob();
    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  private EcliCrawlerDocument getTestDocument() {
    return new EcliCrawlerDocument(
        "docNumber", "docNumber.xml", "ECLI:DE:XX:2025:1111111", "BGH", "2025-01-01", "url", true);
  }

  private Sitemap getTestSitemap(EcliCrawlerDocument doc) {
    var sitemap = new Sitemap().setName("name");
    EcliCrawlerDocumentMapper.toSitemapUrl(doc);
    return sitemap.setUrl(List.of(EcliCrawlerDocumentMapper.toSitemapUrl(doc)));
  }

  @Test
  void itWritesFullDiffOnInitialRun()
      throws JAXBException, FileNotFoundException, ObjectStoreServiceException {
    List<String> changelogPaths = List.of("changelog0.xml", "changelog1.xml");
    LocalDate day = LocalDate.now();
    var testEcliDocument = getTestDocument();
    var testSitemap = getTestSitemap(testEcliDocument);

    when(sitemapService.getSitemapFilesPathsForDay(PATH_PREFIX, day)).thenReturn(List.of());
    when(syncJob.getNewChangelogs(caseLawBucket, "0")).thenReturn(changelogPaths);
    when(documentService.getFullDiff()).thenReturn(List.of(testEcliDocument));

    configurePersistenceMocks(
        day,
        List.of(testEcliDocument),
        List.of(testSitemap),
        List.of(new Sitemapindex().setName("2025-09-09")));

    Job.ReturnCode code = sitemapJob.runJob();

    verify(sitemapService).writeUrlsToSitemaps(PATH_PREFIX, day, testSitemap.getUrl());
    verifyPersistenceMocks(
        day,
        List.of(testEcliDocument),
        List.of(testSitemap),
        List.of(new Sitemapindex().setName("2025-09-09")));

    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  @Test
  void itWritesIncrementalChangelogDiff()
      throws JAXBException, FileNotFoundException, ObjectStoreServiceException {
    List<String> changelogPaths = List.of("changelog0.xml", "changelog1.xml");
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file1")));
    Changelog log2 = new Changelog();
    log2.setChanged(new HashSet<>(List.of("file2")));
    List<Changelog> changelogs = List.of(log1, log2);
    LocalDate day = LocalDate.now();
    var testEcliDocument = getTestDocument();
    var testSitemap = getTestSitemap(testEcliDocument);
    var testIndex = new Sitemapindex().setName("2025-09-09");

    when(sitemapService.getSitemapFilesPathsForDay(PATH_PREFIX, day)).thenReturn(List.of());
    when(portalBucket.getAllKeysByPrefix(LAST_PROCESSED_CHANGELOG)).thenReturn(List.of("file"));
    when(portalBucket.getFileAsString(LAST_PROCESSED_CHANGELOG))
        .thenReturn(Optional.of("changelog/date"));
    when(syncJob.getNewChangelogs(caseLawBucket, "changelog/date")).thenReturn(changelogPaths);

    when(syncJob.parseOneChangelog(caseLawBucket, "changelog0.xml"))
        .thenReturn(changelogs.getFirst());
    when(syncJob.parseOneChangelog(caseLawBucket, "changelog1.xml")).thenReturn(changelogs.get(1));

    when(documentService.getFromChangelogs(changelogs)).thenReturn(List.of(testEcliDocument));

    configurePersistenceMocks(
        day, List.of(testEcliDocument), List.of(testSitemap), List.of(testIndex));

    Job.ReturnCode code = sitemapJob.runJob();

    verify(sitemapService, never()).writeRobotsTxt(ROBOTS_TXT_PATH);
    verifyPersistenceMocks(
        day, List.of(testEcliDocument), List.of(testSitemap), List.of(testIndex));

    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  private void configurePersistenceMocks(
      LocalDate day,
      List<EcliCrawlerDocument> documents,
      List<Sitemap> sitemaps,
      List<Sitemapindex> indices)
      throws JAXBException, FileNotFoundException, ObjectStoreServiceException {
    List<Url> urls = sitemaps.stream().flatMap(sitemap -> sitemap.getUrl().stream()).toList();

    when(sitemapService.writeUrlsToSitemaps(
            eq(PATH_PREFIX), eq(day), argThat(new UrlListMatcher(urls))))
        .thenReturn(sitemaps);
    when(sitemapService.writeSitemapsIndices(
            PATH_PREFIX, "frontend/url/api/v1/eclicrawler/", day, sitemaps))
        .thenReturn(indices);

    Mockito.doNothing()
        .when(documentService)
        .saveAll(argThat(new EcliCrawlerDocumentsMatcher(documents)));

    Mockito.doNothing()
        .when(sitemapService)
        .updateRobotsTxt(ROBOTS_TXT_PATH, "frontend/url/api/v1/eclicrawler/", indices);
  }

  private void verifyPersistenceMocks(
      LocalDate day,
      List<EcliCrawlerDocument> documents,
      List<Sitemap> sitemaps,
      List<Sitemapindex> indices)
      throws JAXBException, FileNotFoundException, ObjectStoreServiceException {
    List<Url> urls = sitemaps.stream().flatMap(sitemap -> sitemap.getUrl().stream()).toList();

    verify(sitemapService).writeUrlsToSitemaps(PATH_PREFIX, day, urls);
    verify(sitemapService)
        .writeSitemapsIndices(PATH_PREFIX, "frontend/url/api/v1/eclicrawler/", day, sitemaps);
    verify(documentService).saveAll(documents);
    verify(sitemapService)
        .updateRobotsTxt(ROBOTS_TXT_PATH, "frontend/url/api/v1/eclicrawler/", indices);
  }
}
