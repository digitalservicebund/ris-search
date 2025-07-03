package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.Sitemap;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DailySitemapJob {

  SitemapService sitemapService;

  IndexSyncJob indexJob;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  IndexStatusService indexStatusService;

  CaseLawService caseLawService;

  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String STATUS_FILE = "caselaw_sitemaps_status.json";

  public DailySitemapJob(
      SitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      IndexStatusService indexStatusService,
      CaseLawService caseLawService) {
    this.sitemapService = service;
    this.indexJob = indexJob;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexStatusService = indexStatusService;
    this.caseLawService = caseLawService;
  }

  public void run(LocalDate cutOffDate)
      throws ObjectStoreServiceException, FatalDailySitemapJobException {

    if (!sitemapService.getSitemapFilesPathsForDay(cutOffDate).isEmpty()) {
      throw new FatalDailySitemapJobException(
          String.format("Sitemaps for day %s already exists", cutOffDate.format(formatter)));
    }

    IndexingState state = indexStatusService.loadStatus(STATUS_FILE);
    String lastProcessedChangelogFile = state.lastProcessedChangelogFile();

    if (Objects.isNull(lastProcessedChangelogFile)) {
      createAll();
    } else {
      String lastSuccesfulIndexingJob =
          indexStatusService
              .loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME)
              .lastProcessedChangelogFile();

      boolean indexingIsFinished =
          !Objects.isNull(lastSuccesfulIndexingJob)
              && lastSuccesfulIndexingJob.compareTo(lastProcessedChangelogFile) >= 0;
      if (indexingIsFinished) {
        List<String> newChangelogPaths =
            indexJob.getNewChangelogs(caselawbucket, lastProcessedChangelogFile);
        createFromChangelogs(newChangelogPaths, cutOffDate);
      }
    }
  }

  private Optional<String> createSitemaps(List<ChangedDocument> changed, LocalDate cutoffDate)
      throws JAXBException {

    List<Sitemap> urlSets = sitemapService.createSitemaps(changed);
    return sitemapService.writeSitemapFiles(urlSets, cutoffDate);
  }

  private void createAll() throws FatalDailySitemapJobException {
    LocalDate yesterday = LocalDate.now().minusDays(1);

    List<ChangedDocument> documents =
        new ArrayList<>(
            caseLawService.getAllEcliDocuments().stream().map(CreatedDocument::new).toList());

    try {
      Optional<String> sitemapOption = createSitemaps(documents, yesterday);
      sitemapOption.ifPresent(
          sitemap -> {
            sitemapService.writeRobotsTxt(sitemap);
          });
      indexStatusService.saveStatus(
          STATUS_FILE,
          new IndexingState()
              .withLastProcessedChangelogFile(
                  IndexSyncJob.CHANGELOGS_PREFIX + Instant.now().toString()));
    } catch (JAXBException ex) {
      throw new FatalDailySitemapJobException(ex.getMessage());
    }
  }

  private void createFromChangelogs(List<String> filePaths, LocalDate cutoffDate)
      throws ObjectStoreServiceException, FatalDailySitemapJobException {

    var changelogsUpToCutoff =
        filePaths.stream()
            .filter(
                (e) -> {
                  int datetimeStringOffset = IndexSyncJob.CHANGELOGS_PREFIX.length();
                  String datetimeString =
                      e.substring(datetimeStringOffset, datetimeStringOffset + 10);
                  LocalDate time = LocalDate.parse(datetimeString, formatter);

                  return time.isBefore(cutoffDate) || time.isEqual(cutoffDate);
                })
            .toList();

    List<ChangedDocument> changedDocuments = new ArrayList<>();
    for (String changelogPath : changelogsUpToCutoff) {
      Optional<Changelog> logOptional =
          Optional.ofNullable(this.indexJob.parseOneChangelog(caselawbucket, changelogPath));
      logOptional.ifPresent(
          log -> {
            List<String> documentIdentifiers =
                log.getChanged().stream().map(c -> c.replace(".xml", "")).toList();
            List<CreatedDocument> createdDocs =
                caseLawService.getEcliDocumentsByDocumentNumbers(documentIdentifiers).stream()
                    .map(CreatedDocument::new)
                    .toList();
            changedDocuments.addAll(createdDocs);
            List<DeletedDocument> deletedDocs =
                log.getDeleted().stream()
                    .map(d -> new DeletedDocument(d.replace(".xml", "")))
                    .toList();
            changedDocuments.addAll(deletedDocs);
          });
    }
    try {
      if (!changelogsUpToCutoff.isEmpty()) {
        Optional<String> sitemapIndexPathOption = createSitemaps(changedDocuments, cutoffDate);
        if (sitemapIndexPathOption.isPresent()) {
          sitemapService.updateRobotsTxt(sitemapIndexPathOption.get());
        }

        indexStatusService.saveStatus(
            STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(filePaths.getLast()));
      }
    } catch (FileNotFoundException | JAXBException ex) {
      throw new FatalDailySitemapJobException(ex.getMessage());
    }
  }
}
