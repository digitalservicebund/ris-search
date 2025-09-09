package de.bund.digitalservice.ris.search.unit.sitemap.eclicrawler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DailyEcliSitemapJob;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliSitemapService;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.eclipse.persistence.exceptions.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailySitemapJobTest {

  DailyEcliSitemapJob sitemapJob;
  @Mock EcliSitemapService sitemapService;
  @Mock CaseLawIndexSyncJob syncJob;
  @Mock PortalBucket portalBucket;
  @Mock CaseLawBucket caseLawBucket;
  @Mock IndexStatusService indexStatusService;
  @Mock CaseLawRepository caselawRepo;
  @Mock EcliCrawlerDocumentRepository repository;

  @BeforeEach
  void setup() {
    sitemapJob =
        new DailyEcliSitemapJob(
            sitemapService,
            syncJob,
            portalBucket,
            caseLawBucket,
            indexStatusService,
            caselawRepo,
            repository);
  }

  @Test
  void itOnlyProcessesNewChangelogFiles() throws ObjectStoreServiceException {
    Instant timestamp = Instant.now().minus(1, ChronoUnit.DAYS);
    String lastProcessedChangelog = "changelogs/" + timestamp;
    String lastSuccesfullIndexJob = "changelogs/" + timestamp;
    LocalDate date = LocalDate.now();
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());
    Mockito.when(indexStatusService.loadStatus(DailyEcliSitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState(lastSuccesfullIndexJob, null, null));
    sitemapJob.runJob();

    Mockito.verify(syncJob, Mockito.never()).getNewChangelogs(any(), any());
  }

  @Test
  void itWritesSuccessfulOnValidChangelogWithEmptyDocumentationUnits()
      throws ObjectStoreServiceException {
    Instant lastProcessedTimestamp = Instant.now().minus(1, ChronoUnit.DAYS);
    Instant changelogDate = Instant.now().minus(2, ChronoUnit.DAYS);
    String lastProcessedSitemapJobChangelog = "changelogs/" + lastProcessedTimestamp;
    String lastSuccesfulIndexJob = "changelogs/" + Instant.now();
    String newChangelogPath = "changelogs/" + changelogDate;
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now())).thenReturn(List.of());

    Mockito.when(indexStatusService.loadStatus(DailyEcliSitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedSitemapJobChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
    Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedSitemapJobChangelog))
        .thenReturn(List.of(newChangelogPath));

    Mockito.verify(indexStatusService, Mockito.atMostOnce())
        .saveStatus(
            DailyEcliSitemapJob.STATUS_FILE,
            new IndexingState().withLastProcessedChangelogFile(newChangelogPath));
    sitemapJob.runJob();
  }

  @Test
  void itWritesInitialSitemapFiles()
      throws ObjectStoreServiceException, JAXBException, jakarta.xml.bind.JAXBException {

    Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now())).thenReturn(List.of());
    Mockito.when(indexStatusService.loadStatus(DailyEcliSitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(null, null, null));

    Mockito.when(caseLawBucket.getAllKeys()).thenReturn(List.of("docnumber.xml"));

    List<CaseLawDocumentationUnit> documents =
        List.of(CaseLawDocumentationUnit.builder().id("id").decisionDate(LocalDate.now()).build());
    Mockito.when(caselawRepo.findAllValidFederalEcliDocumentsIn(List.of("docnumber")))
        .thenReturn(documents);
    List<Sitemap> sitemaps = List.of(new Sitemap());
    Mockito.when(
            sitemapService.createSitemaps(
                argThat(
                    arg -> {
                      EcliCrawlerDocument doc = arg.getFirst();
                      return Objects.equals(doc.getId(), "id");
                    })))
        .thenReturn(sitemaps);

    String expectedSitemapIndexPath = "expected_path/sitemap_index_1.xml";
    Mockito.when(sitemapService.writeSitemapFiles(sitemaps, LocalDate.now()))
        .thenReturn(List.of(expectedSitemapIndexPath));

    sitemapJob.runJob();

    Mockito.verify(sitemapService).writeRobotsTxt(List.of(expectedSitemapIndexPath));
    Mockito.verify(indexStatusService, Mockito.atMostOnce())
        .saveStatus(
            DailyEcliSitemapJob.STATUS_FILE,
            new IndexingState().withLastProcessedChangelogFile(""));
  }

  @Test
  void itWritesSitemapFilesFromChangelog()
      throws ObjectStoreServiceException,
          JAXBException,
          FileNotFoundException,
          jakarta.xml.bind.JAXBException {
    Instant lastProcessedTimestamp = Instant.now().minus(2, ChronoUnit.DAYS);
    Instant newChangelogTimestamp = Instant.now().minus(1, ChronoUnit.DAYS);
    String lastProcessedSitemapJobChangelog = "changelogs/" + lastProcessedTimestamp;
    String lastSuccesfulIndexJob = "changelogs/" + newChangelogTimestamp;
    String newChangelogPath = "changelogs/" + newChangelogTimestamp;
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now())).thenReturn(List.of());

    Mockito.when(indexStatusService.loadStatus(DailyEcliSitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedSitemapJobChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
    Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedSitemapJobChangelog))
        .thenReturn(List.of(newChangelogPath));

    Changelog newChangelog = new Changelog();
    newChangelog.setChanged(new HashSet<>(List.of("ABCD.xml")));
    newChangelog.setDeleted(new HashSet<>(List.of("DELETED.xml")));

    List<Sitemap> urlSets = List.of(new Sitemap());
    Mockito.when(syncJob.parseOneChangelog(caseLawBucket, newChangelogPath))
        .thenReturn(newChangelog);

    Mockito.when(caselawRepo.findAllValidFederalEcliDocumentsIn(List.of("ABCD")))
        .thenReturn(
            List.of(
                CaseLawDocumentationUnit.builder()
                    .id("ABCD")
                    .decisionDate(LocalDate.now())
                    .build()));
    EcliCrawlerDocument alreadyExistingMetadata = new EcliCrawlerDocument();
    alreadyExistingMetadata.setId("DELETED");
    Mockito.when(repository.findAllByIsPublishedIsTrueAndIdIn(List.of("DELETED")))
        .thenReturn(List.of(alreadyExistingMetadata));

    Mockito.when(
            sitemapService.createSitemaps(
                argThat(arg -> arg.getFirst().isPublished() && !arg.getLast().isPublished())))
        .thenReturn(urlSets);
    String expectedSitemapIndexPath = "expected/sitemap_index_1.xml";
    Mockito.when(sitemapService.writeSitemapFiles(urlSets, LocalDate.now()))
        .thenReturn(List.of(expectedSitemapIndexPath));

    sitemapJob.runJob();

    Mockito.verify(sitemapService).updateRobotsTxt(List.of(expectedSitemapIndexPath));
    Mockito.verify(indexStatusService, Mockito.atMostOnce())
        .saveStatus(
            DailyEcliSitemapJob.STATUS_FILE,
            new IndexingState().withLastProcessedChangelogFile(newChangelogPath));
  }

  @Test
  void itReturnsAnErrorWhenDayWasAlreadyGenerated() {
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now()))
        .thenReturn(List.of("alreadyexists"));

    Assertions.assertEquals(Job.ReturnCode.ERROR, sitemapJob.runJob());
  }
}
