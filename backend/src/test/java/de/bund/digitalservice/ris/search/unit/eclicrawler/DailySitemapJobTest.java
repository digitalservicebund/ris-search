package de.bund.digitalservice.ris.search.unit.eclicrawler;

import de.bund.digitalservice.ris.search.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.eclicrawler.service.EcliSitemapJob;
import de.bund.digitalservice.ris.search.eclicrawler.service.EcliSitemapService;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailySitemapJobTest {

  EcliSitemapJob sitemapJob;
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
        new EcliSitemapJob(
            sitemapService, portalBucket, caseLawBucket, repository, syncJob, "frontent/url");
  }

  @Test
  void itOnlyProcessesNewChangelogFiles() throws ObjectStoreServiceException {
    /**
     * Instant timestamp = Instant.now().minus(1, ChronoUnit.DAYS); String lastProcessedChangelog =
     * "changelogs/" + timestamp; String lastSuccesfullIndexJob = "changelogs/" + timestamp;
     * LocalDate date = LocalDate.now();
     * Mockito.when(sitemapService.getSitemapFilesPathsForDay(date)).thenReturn(List.of());
     * Mockito.when(indexStatusService.loadStatus(EcliSitemapJob.STATUS_FILE)) .thenReturn(new
     * IndexingState(lastProcessedChangelog, null, null));
     * Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
     * .thenReturn(new IndexingState(lastSuccesfullIndexJob, null, null)); sitemapJob.runJob();
     *
     * <p>Mockito.verify(syncJob, Mockito.never()).getNewChangelogs(any(), any());
     */
  }

  @Test
  void itWritesSuccessfulOnValidChangelogWithEmptyDocumentationUnits()
      throws ObjectStoreServiceException {
    /**
     * Instant lastProcessedTimestamp = Instant.now().minus(1, ChronoUnit.DAYS); Instant
     * changelogDate = Instant.now().minus(2, ChronoUnit.DAYS); String
     * lastProcessedSitemapJobChangelog = "changelogs/" + lastProcessedTimestamp; String
     * lastSuccesfulIndexJob = "changelogs/" + Instant.now(); String newChangelogPath =
     * "changelogs/" + changelogDate;
     * Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now())).thenReturn(List.of());
     *
     * <p>Mockito.when(indexStatusService.loadStatus(EcliSitemapJob.STATUS_FILE)) .thenReturn(new
     * IndexingState(lastProcessedSitemapJobChangelog, null, null));
     * Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
     * .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
     * Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedSitemapJobChangelog))
     * .thenReturn(List.of(newChangelogPath));
     *
     * <p>Mockito.verify(indexStatusService, Mockito.atMostOnce()) .saveStatus(
     * EcliSitemapJob.STATUS_FILE, new
     * IndexingState().withLastProcessedChangelogFile(newChangelogPath)); sitemapJob.runJob();
     */
  }
  /**
   * @Test void itWritesInitialSitemapFiles() throws ObjectStoreServiceException, JAXBException,
   * jakarta.xml.bind.JAXBException {
   *
   * <p>Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now())).thenReturn(List.of());
   * Mockito.when(indexStatusService.loadStatus(EcliSitemapJob.STATUS_FILE)) .thenReturn(new
   * IndexingState(null, null, null));
   *
   * <p>Mockito.when(caseLawBucket.getAllKeys()).thenReturn(List.of("docnumber.xml"));
   *
   * <p>List<CaseLawDocumentationUnit> documents =
   * List.of(CaseLawDocumentationUnit.builder().id("id").decisionDate(LocalDate.now()).build());
   * Mockito.when(caselawRepo.findAllValidFederalEcliDocumentsIn(List.of("docnumber")))
   * .thenReturn(documents); List<Sitemap> sitemaps = List.of(new Sitemap()); Mockito.when(
   * sitemapService.createSitemaps( argThat( arg -> { EcliCrawlerDocument doc = arg.getFirst();
   * return Objects.equals(doc.getId(), "id"); }))) .thenReturn(sitemaps);
   *
   * <p>String expectedSitemapIndexPath = "expected_path/sitemap_index_1.xml";
   * Mockito.when(sitemapService.writeSitemapFiles(sitemaps, LocalDate.now()))
   * .thenReturn(List.of(expectedSitemapIndexPath));
   *
   * <p>sitemapJob.runJob();
   *
   * <p>Mockito.verify(sitemapService).writeRobotsTxt(List.of(expectedSitemapIndexPath));
   * Mockito.verify(indexStatusService, Mockito.atMostOnce()) .saveStatus(
   * EcliSitemapJob.STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(""));
   *
   * <p>} @Test void itWritesSitemapFilesFromChangelog() throws ObjectStoreServiceException,
   * JAXBException, FileNotFoundException, jakarta.xml.bind.JAXBException {
   *
   * <p>Instant lastProcessedTimestamp = Instant.now().minus(2, ChronoUnit.DAYS); Instant
   * newChangelogTimestamp = Instant.now().minus(1, ChronoUnit.DAYS); String
   * lastProcessedSitemapJobChangelog = "changelogs/" + lastProcessedTimestamp; String
   * lastSuccesfulIndexJob = "changelogs/" + newChangelogTimestamp; String newChangelogPath =
   * "changelogs/" + newChangelogTimestamp;
   * Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now())).thenReturn(List.of());
   *
   * <p>Mockito.when(indexStatusService.loadStatus(EcliSitemapJob.STATUS_FILE)) .thenReturn(new
   * IndexingState(lastProcessedSitemapJobChangelog, null, null));
   * Mockito.when(indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME))
   * .thenReturn(new IndexingState(lastSuccesfulIndexJob, null, null));
   * Mockito.when(syncJob.getNewChangelogs(caseLawBucket, lastProcessedSitemapJobChangelog))
   * .thenReturn(List.of(newChangelogPath));
   *
   * <p>Changelog newChangelog = new Changelog(); newChangelog.setChanged(new
   * HashSet<>(List.of("ABCD.xml"))); newChangelog.setDeleted(new
   * HashSet<>(List.of("DELETED.xml")));
   *
   * <p>List<Sitemap> urlSets = List.of(new Sitemap());
   * Mockito.when(syncJob.parseOneChangelog(caseLawBucket, newChangelogPath))
   * .thenReturn(newChangelog);
   *
   * <p>Mockito.when(caselawRepo.findAllValidFederalEcliDocumentsIn(List.of("ABCD"))) .thenReturn(
   * List.of( CaseLawDocumentationUnit.builder() .id("ABCD") .decisionDate(LocalDate.now())
   * .build())); EcliCrawlerDocument alreadyExistingMetadata = new EcliCrawlerDocument();
   * alreadyExistingMetadata.setId("DELETED");
   * Mockito.when(repository.findAllByIsPublishedIsTrueAndIdIn(List.of("DELETED")))
   * .thenReturn(List.of(alreadyExistingMetadata));
   *
   * <p>Mockito.when( sitemapService.createSitemaps( argThat(arg -> arg.getFirst().isPublished() &&
   * !arg.getLast().isPublished()))) .thenReturn(urlSets); String expectedSitemapIndexPath =
   * "expected/sitemap_index_1.xml"; Mockito.when(sitemapService.writeSitemapFiles(urlSets,
   * LocalDate.now())) .thenReturn(List.of(expectedSitemapIndexPath));
   *
   * <p>sitemapJob.runJob();
   *
   * <p>Mockito.verify(sitemapService).updateRobotsTxt(List.of(expectedSitemapIndexPath));
   * Mockito.verify(indexStatusService, Mockito.atMostOnce()) .saveStatus(
   * EcliSitemapJob.STATUS_FILE, new
   * IndexingState().withLastProcessedChangelogFile(newChangelogPath)); } @Test void
   * itReturnsAnErrorWhenDayWasAlreadyGenerated() {
   * Mockito.when(sitemapService.getSitemapFilesPathsForDay(LocalDate.now()))
   * .thenReturn(List.of("alreadyexists"));
   *
   * <p>Assertions.assertEquals(Job.ReturnCode.ERROR, sitemapJob.runJob()); }
   */
}
