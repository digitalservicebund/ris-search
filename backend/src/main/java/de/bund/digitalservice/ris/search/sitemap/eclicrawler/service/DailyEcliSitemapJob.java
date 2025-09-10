package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.service.Job;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper.EcliCrawlerDocumentMapper;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliCrawlerDocumentRepository;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Sitemap;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class DailyEcliSitemapJob implements Job {

  EcliSitemapService sitemapService;

  IndexSyncJob indexJob;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  IndexStatusService indexStatusService;

  CaseLawRepository caseLawRepo;

  EcliCrawlerDocumentRepository repository;

  public static final String STATUS_FILE = "ecli_sitemaps_status.json";

  private final LocalDate now = LocalDate.now();

  public DailyEcliSitemapJob(
      EcliSitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      IndexStatusService indexStatusService,
      CaseLawRepository caseLawRepo,
      EcliCrawlerDocumentRepository repository) {
    this.sitemapService = service;
    this.indexJob = indexJob;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexStatusService = indexStatusService;
    this.caseLawRepo = caseLawRepo;
    this.repository = repository;
  }

  public ReturnCode runJob() {
    try {
      if (!sitemapService.getSitemapFilesPathsForDay(now).isEmpty()) {
        return ReturnCode.ERROR;
      }

      IndexingState state = indexStatusService.loadStatus(STATUS_FILE);
      String lastProcessedEcliChangelogFile = state.lastProcessedChangelogFile();

      if (Objects.isNull(lastProcessedEcliChangelogFile)) {
        createAll();
      } else {
        String lastSuccesfulIndexingJob =
            indexStatusService
                .loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME)
                .lastProcessedChangelogFile();

        boolean indexingIsFinished =
            !Objects.isNull(lastSuccesfulIndexingJob)
                && lastSuccesfulIndexingJob.compareTo(lastProcessedEcliChangelogFile) > 0;
        if (indexingIsFinished) {
          List<String> newChangelogPaths =
              indexJob.getNewChangelogs(caselawbucket, lastProcessedEcliChangelogFile);
          createFromChangelogs(newChangelogPaths);
        }
      }
    } catch (JAXBException | FileNotFoundException | ObjectStoreServiceException e) {
      return ReturnCode.ERROR;
    }

    return ReturnCode.SUCCESS;
  }

  private List<String> writeSitemaps(List<EcliCrawlerDocument> ecliDocuments, LocalDate cutoffDate)
      throws JAXBException {

    List<Sitemap> urlSets = sitemapService.createSitemaps(ecliDocuments);
    List<String> indexPaths = sitemapService.writeSitemapFiles(urlSets, cutoffDate);
    if (!indexPaths.isEmpty()) {
      repository.saveAll(ecliDocuments);
    }
    return indexPaths;
  }

  private void createAll() throws JAXBException {

    List<String> allDocnumbers =
        caselawbucket.getAllKeys().stream().map(s -> s.replace(".xml", "")).toList();
    List<List<String>> partitionedDocnumbers = ListUtils.partition(allDocnumbers, 10000);
    List<EcliCrawlerDocument> allDocunits = new ArrayList<>();

    for (List<String> docNumbers : partitionedDocnumbers) {
      allDocunits.addAll(
          caseLawRepo.findAllValidFederalEcliDocumentsIn(docNumbers).stream()
              .map(EcliCrawlerDocumentMapper::fromCaseLawDocumentationUnit)
              .toList());
    }

    List<String> sitemapIndexPaths = writeSitemaps(allDocunits, now);
    if (!sitemapIndexPaths.isEmpty()) {
      sitemapService.writeRobotsTxt(sitemapIndexPaths);
    }
    indexStatusService.saveStatus(
        STATUS_FILE,
        new IndexingState()
            .withLastProcessedChangelogFile(
                IndexSyncJob.CHANGELOGS_PREFIX + Instant.now().toString()));
  }

  private void createFromChangelogs(List<String> filePaths)
      throws ObjectStoreServiceException, FileNotFoundException, JAXBException {

    List<EcliCrawlerDocument> ecliDocuments = getAllChangesFromChangelogs(filePaths);
    if (!ecliDocuments.isEmpty()) {
      List<String> sitemapIndexPaths = writeSitemaps(ecliDocuments, now);
      sitemapService.updateRobotsTxt(sitemapIndexPaths);

      indexStatusService.saveStatus(
          STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(filePaths.getLast()));
    }
  }

  private List<EcliCrawlerDocument> getAllChangesFromChangelogs(List<String> changelogPaths)
      throws ObjectStoreServiceException {

    List<Changelog> changelogs = new ArrayList<>();
    for (String changelogPath : changelogPaths) {
      Optional<Changelog> logOptional =
          Optional.ofNullable(this.indexJob.parseOneChangelog(caselawbucket, changelogPath));
      logOptional.ifPresent(changelogs::add);
    }
    Changelog mergedChangelog = ChangelogParser.mergeChangelogs(changelogs);

    List<String> createIdentifiers =
        mergedChangelog.getChanged().stream().map(i -> i.replace(".xml", "")).toList();
    List<EcliCrawlerDocument> changes =
        new ArrayList<>(
            caseLawRepo.findAllValidFederalEcliDocumentsIn(createIdentifiers).stream()
                .map(EcliCrawlerDocumentMapper::fromCaseLawDocumentationUnit)
                .toList());

    List<String> deleteIdentifiers =
        mergedChangelog.getDeleted().stream().map(i -> i.replace(".xml", "")).toList();
    changes.addAll(
        repository.findAllByIsPublishedIsTrueAndIdIn(deleteIdentifiers).stream()
            .map(
                ecliSitemap -> {
                  ecliSitemap.setPublished(false);
                  return ecliSitemap;
                })
            .toList());
    return changes;
  }
}
