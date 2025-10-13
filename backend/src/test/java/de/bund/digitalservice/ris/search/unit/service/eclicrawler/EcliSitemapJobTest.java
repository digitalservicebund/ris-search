package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import static de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob.LAST_PROCESSED_CHANGELOG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliCrawlerDocumentService;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapWriter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EcliSitemapJobTest {

  EcliSitemapJob sitemapJob;
  @Mock EcliSitemapWriter sitemapWriter;
  @Mock CaseLawIndexSyncJob syncJob;
  @Mock PortalBucket portalBucket;
  @Mock CaseLawBucket caseLawBucket;
  @Mock EcliCrawlerDocumentService documentService;
  String apiUrl = "frontend/url/";

  @BeforeEach
  void setup() {
    sitemapJob =
        new EcliSitemapJob(
            sitemapWriter, portalBucket, caseLawBucket, syncJob, documentService, apiUrl);
  }

  @Test
  void itReturnsSuccessfulIfItRanAlready() {
    LocalDate day = LocalDate.now();
    when(sitemapWriter.getSitemapFilesPathsForDay(day)).thenReturn(List.of("2025/01/01"));

    Job.ReturnCode code = sitemapJob.runJob();
    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  @Test
  void itWritesFullDiffOnInitialRun() {
    List<String> changelogPaths = List.of("changelog0.xml", "changelog1.xml");
    LocalDate day = LocalDate.now();
    when(sitemapWriter.getSitemapFilesPathsForDay(day)).thenReturn(List.of());
    when(syncJob.getNewChangelogs(caseLawBucket, "0")).thenReturn(changelogPaths);

    Job.ReturnCode code = sitemapJob.runJob();

    verify(documentService).writeFullDiff(apiUrl + "v1/eclicrawler/", day);

    assertEquals(Job.ReturnCode.SUCCESS, code);
  }

  @Test
  void itWritesIncrementalChangelogDiff() throws ObjectStoreServiceException {
    List<String> changelogPaths = List.of("changelog0.xml");
    Changelog log1 = new Changelog();
    log1.setChanged(new HashSet<>(List.of("file1")));
    LocalDate day = LocalDate.now();

    when(sitemapWriter.getSitemapFilesPathsForDay(day)).thenReturn(List.of());
    when(portalBucket.getAllKeysByPrefix(LAST_PROCESSED_CHANGELOG)).thenReturn(List.of("file"));
    when(portalBucket.getFileAsString(LAST_PROCESSED_CHANGELOG))
        .thenReturn(Optional.of("changelog/date"));
    when(syncJob.getNewChangelogs(caseLawBucket, "changelog/date")).thenReturn(changelogPaths);

    when(syncJob.parseOneChangelog(caseLawBucket, "changelog0.xml")).thenReturn(log1);

    Job.ReturnCode code = sitemapJob.runJob();

    verify(documentService)
        .writeFromChangelog(
            eq(apiUrl + "v1/eclicrawler/"),
            eq(day),
            argThat(
                new ArgumentMatcher<Changelog>() {
                  @Override
                  public boolean matches(Changelog changelog) {
                    return changelog.getChanged().equals(log1.getChanged());
                  }
                }));

    assertEquals(Job.ReturnCode.SUCCESS, code);
  }
}
