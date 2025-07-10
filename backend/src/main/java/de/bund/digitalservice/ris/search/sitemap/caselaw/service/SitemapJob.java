package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Sitemapindex;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.UrlSet;
import jakarta.xml.bind.JAXBException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SitemapJob {

  SitemapService sitemapService;

  IndexSyncJob indexJob;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  IndexStatusService indexStatusService;

  IndexCaselawService indexCaselawService;

  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String STATUS_FILE = "caselaw_sitemaps_status.json";

  public SitemapJob(
      SitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      IndexStatusService indexStatusService,
      IndexCaselawService indexCaselawService) {
    this.sitemapService = service;
    this.indexJob = indexJob;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexStatusService = indexStatusService;
    this.indexCaselawService = indexCaselawService;
  }

  public void run() throws JAXBException, JsonProcessingException, ObjectStoreServiceException {

    IndexingState state = indexStatusService.loadStatus(STATUS_FILE);
    String lastProcessedChangelogFile = state.lastProcessedChangelogFile();

    if (Objects.isNull(lastProcessedChangelogFile)) {
      createAll();
    } else {
      createFromChangelogs(indexJob.getNewChangelogs(caselawbucket, lastProcessedChangelogFile));
    }
  }

  private void createSitemaps(HashSet<String> changed, HashSet<String> deleted)
      throws JAXBException {
    LocalDate now = LocalDate.now();
    List<UrlSet> urlSets = sitemapService.createUrlSets(changed, deleted);
    List<String> urlSetLocations = sitemapService.writeUrlSets(urlSets, now);

    if (!urlSetLocations.isEmpty()) {
      Sitemapindex index = sitemapService.createSitemapIndex(urlSetLocations);
      sitemapService.writeSitemapIndex(index, now);
    }
  }

  private void createAll() throws JAXBException {
    HashSet<String> filenames =
        new HashSet<>(
            indexCaselawService.getAllCaseLawFilenames().stream()
                .map(name -> name.replace(".xml", ""))
                .toList());
    createSitemaps(filenames, new HashSet<>());
    indexStatusService.saveStatus(
        STATUS_FILE,
        new IndexingState()
            .withLastProcessedChangelogFile(
                IndexSyncJob.CHANGELOGS_PREFIX + Instant.now().toString()));
  }

  private void createFromChangelogs(List<String> filePaths) throws JAXBException {

    var changelogsUpToYesterday =
        filePaths.stream()
            .filter(
                (e) -> {
                  int datetimeStringOffset = IndexSyncJob.CHANGELOGS_PREFIX.length();
                  String datetimeString =
                      e.substring(datetimeStringOffset, datetimeStringOffset + 10);
                  LocalDate time = LocalDate.parse(datetimeString, formatter);

                  return time.isBefore(LocalDate.now());
                })
            .toList();

    HashSet<String> changed = new HashSet<>();
    HashSet<String> deleted = new HashSet<>();
    changelogsUpToYesterday.forEach(
        file -> {
          try {
            Optional<Changelog> logOptional =
                Optional.ofNullable(this.indexJob.parseOneChangelog(caselawbucket, file));
            logOptional.ifPresent(
                log -> {
                  changed.addAll(log.getChanged());
                  deleted.addAll(log.getDeleted());
                });
          } catch (ObjectStoreServiceException e) {
            // log error
          }
        });

    if (!changelogsUpToYesterday.isEmpty()) {
      createSitemaps(changed, deleted);

      indexStatusService.saveStatus(
          STATUS_FILE, new IndexingState().withLastProcessedChangelogFile(filePaths.getLast()));
    }
  }
}
