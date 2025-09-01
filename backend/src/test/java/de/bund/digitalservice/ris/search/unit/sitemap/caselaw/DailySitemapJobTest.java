package de.bund.digitalservice.ris.search.unit.sitemap.caselaw;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliDocumentRepository;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.CreatedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DailySitemapJob;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.DeletedDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.FatalDailySitemapJobException;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.SitemapService;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailySitemapJobTest {

  DailySitemapJob sitemapJob;
  @Mock SitemapService sitemapService;
  @Mock CaseLawIndexSyncJob syncJob;
  @Mock PortalBucket portalBucket;
  @Mock CaseLawBucket caseLawBucket;
  @Mock IndexStatusService indexStatusService;
  @Mock CaseLawService caseLawService;
  @Mock EcliDocumentRepository repository;

  @BeforeEach
  void setup() {
    sitemapJob =
        new DailySitemapJob(
            sitemapService,
            syncJob,
            portalBucket,
            caseLawBucket,
            indexStatusService,
            caseLawService,
            repository);
  }

  @Test
  void itOnlyProcessesNewChangelogFiles()
      throws JAXBException,
          FileNotFoundException,
          ObjectStoreServiceException,
          FatalDailySitemapJobException {
    String lastProcessedChangelog = "changelogs/2025-02-01T12:40:58.683244Z";
    String lastSuccesfulIndexJob = "changelogs/2025-02-03T12:40:58.683244Z";
    LocalDate date = LocalDate.of(2025, 2, 2);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());
    Mockito.when(indexStatusService.loadStatus(DailySitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
    Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedChangelog))
        .thenReturn(List.of());
    sitemapJob.run(date);

    Mockito.verify(sitemapService, Mockito.never()).writeSitemapFiles(any(), any());
    Mockito.verify(sitemapService, Mockito.never()).writeRobotsTxt(any());
    Mockito.verify(sitemapService, Mockito.never()).updateRobotsTxt(any());
    Mockito.verify(indexStatusService, Mockito.never())
        .saveStatus(eq(DailySitemapJob.STATUS_FILE), any());
  }

  @Test
  void itIgnoresChangelogFilesAfterCutoff()
      throws JAXBException,
          ObjectStoreServiceException,
          FatalDailySitemapJobException,
          FileNotFoundException {
    String lastProcessedSitemapJobChangelog = "changelogs/2025-02-01T12:40:58.683244Z";
    String lastSuccesfulIndexJob = "changelogs/2025-02-03T12:40:58.683244Z";
    String newChangelogAfterCutoff = "changelogs/2025-02-03T12:40:58.683244Z";
    LocalDate date = LocalDate.of(2025, 2, 2);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());
    Mockito.when(indexStatusService.loadStatus(DailySitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedSitemapJobChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
    Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedSitemapJobChangelog))
        .thenReturn(List.of(newChangelogAfterCutoff));
    sitemapJob.run(date);

    Mockito.verify(sitemapService, Mockito.never()).writeSitemapFiles(any(), any());
    Mockito.verify(sitemapService, Mockito.never()).updateRobotsTxt(any());
    Mockito.verify(indexStatusService, Mockito.never())
        .saveStatus(eq(DailySitemapJob.STATUS_FILE), any());
  }

  @Test
  void itIgnoresChangelogFilesIfIndexIsNotFinished()
      throws JAXBException,
          FileNotFoundException,
          ObjectStoreServiceException,
          FatalDailySitemapJobException {
    String lastProcessedChangelog = "changelogs/2025-02-02T12:40:58.683244Z";
    LocalDate date = LocalDate.of(2025, 2, 2);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());

    Mockito.when(indexStatusService.loadStatus(DailySitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState("changelogs/2025-02-01T12:40:58.683244Z", null, null));

    sitemapJob.run(date);

    Mockito.verify(syncJob, Mockito.never()).getNewChangelogs(any(), any());
    Mockito.verify(sitemapService, Mockito.never()).writeSitemapFiles(any(), any());
    Mockito.verify(sitemapService, Mockito.never()).updateRobotsTxt(any());
    Mockito.verify(indexStatusService, Mockito.never())
        .saveStatus(eq(DailySitemapJob.STATUS_FILE), any());
  }

  @Test
  void itWritesSuccessfulOnValidChangelogWithEmptyDocumentationUnits()
      throws ObjectStoreServiceException, FatalDailySitemapJobException {
    String lastProcessedSitemapJobChangelog = "changelogs/2025-02-01T12:40:58.683244Z";
    String lastSuccesfulIndexJob = "changelogs/2025-02-03T12:40:58.683244Z";
    String newChangelogPath = "changelogs/2025-02-03T12:40:58.683244Z";
    LocalDate date = LocalDate.of(2025, 2, 3);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());

    Mockito.when(indexStatusService.loadStatus(DailySitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(lastProcessedSitemapJobChangelog, null, null));
    Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
        .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
    Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedSitemapJobChangelog))
        .thenReturn(List.of(newChangelogPath));

    Mockito.verify(indexStatusService, Mockito.atMostOnce())
        .saveStatus(
            DailySitemapJob.STATUS_FILE,
            new IndexingState().withLastProcessedChangelogFile(newChangelogPath));
    sitemapJob.run(date);
  }

  @Test
  void itWritesInitialSitemapFiles()
      throws ObjectStoreServiceException, JAXBException, FatalDailySitemapJobException {
    LocalDate date = LocalDate.of(2025, 2, 3);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());

    Mockito.when(indexStatusService.loadStatus(DailySitemapJob.STATUS_FILE))
        .thenReturn(new IndexingState(null, null, null));

    Stream<CaseLawDocumentationUnit> documents =
        Stream.of(CaseLawDocumentationUnit.builder().id("id").build());
    Mockito.when(caseLawService.getAllEcliDocuments()).thenReturn(documents);
    List<Sitemap> urlSets = List.of(new Sitemap());
    Mockito.when(
            sitemapService.createSitemaps(
                argThat(
                    arg -> {
                      CreatedDocument doc = (CreatedDocument) arg.getFirst();
                      return Objects.equals(doc.docUnit().id(), "id");
                    })))
        .thenReturn(urlSets);
    String expectedSitemapIndexPath = "eclicrawler/2025/3/sitemap_index_1.xml";
    Mockito.when(sitemapService.writeSitemapFiles(urlSets, LocalDate.now().minusDays(1)))
        .thenReturn(Optional.of(expectedSitemapIndexPath));

    sitemapJob.run(date);

    Mockito.verify(sitemapService).writeRobotsTxt(expectedSitemapIndexPath);
    Mockito.verify(indexStatusService, Mockito.atMostOnce())
        .saveStatus(
            DailySitemapJob.STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(""));
  }

  @Test
  void itWritesSitemapFilesFromChangelog()
      throws ObjectStoreServiceException,
          JAXBException,
          FileNotFoundException,
          FatalDailySitemapJobException {
    String lastProcessedSitemapJobChangelog = "changelogs/2025-02-01T12:40:58.683244Z";
    String lastSuccesfulIndexJob = "changelogs/2025-02-03T12:40:58.683244Z";
    String newChangelogPath = "changelogs/2025-02-03T12:40:58.683244Z";
    LocalDate date = LocalDate.of(2025, 2, 3);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());

    Mockito.when(indexStatusService.loadStatus(DailySitemapJob.STATUS_FILE))
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

    Mockito.when(caseLawService.getEcliDocumentsByDocumentNumbers(List.of("ABCD")))
        .thenReturn(List.of(CaseLawDocumentationUnit.builder().id("ABCD").build()));

    Mockito.when(
            sitemapService.createSitemaps(
                argThat(
                    arg ->
                        arg.getFirst().getClass().equals(CreatedDocument.class)
                            && arg.getLast().getClass().equals(DeletedDocument.class))))
        .thenReturn(urlSets);
    String expectedSitemapIndexPath = "eclicrawler/2025/3/sitemap_index_1.xml";
    Mockito.when(sitemapService.writeSitemapFiles(urlSets, date))
        .thenReturn(Optional.of(expectedSitemapIndexPath));

    sitemapJob.run(date);

    Mockito.verify(sitemapService).updateRobotsTxt(expectedSitemapIndexPath);
    Mockito.verify(indexStatusService, Mockito.atMostOnce())
        .saveStatus(
            DailySitemapJob.STATUS_FILE,
            new IndexingState().withLastProcessedChangelogFile(newChangelogPath));
  }

  @Test
  void itThrowsAnErrorWhenDayWasAlreadyGenerated() {
    LocalDate date = LocalDate.of(2025, 2, 3);
    Mockito.when(sitemapService.getSitemapFilesPathsForDay(date))
        .thenReturn(List.of("alreadyexists"));

    Assertions.assertThrows(
        FatalDailySitemapJobException.class,
        () -> {
          sitemapJob.run(date);
        });
  }
}
