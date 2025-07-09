package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexStatusService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexingState;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Sitemapindex;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.UrlSet;
import jakarta.xml.bind.JAXBException;
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

  public static final String LAST_SUCCESS_FILE = "caselaw_sitemaps_last_success";

  public SitemapJob(
      SitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      IndexStatusService indexStatusService) {
    this.sitemapService = service;
    this.indexJob = indexJob;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexStatusService = indexStatusService;
  }

  public void run() throws JAXBException, JsonProcessingException, ObjectStoreServiceException {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    Boolean runWasSuccessfullToday =
        portalBucket
            .getFileAsString(LAST_SUCCESS_FILE)
            .map(
                content -> {
                  LocalDate lastSuccess = LocalDate.parse(content, formatter);
                  return lastSuccess.equals(LocalDate.now());
                })
            .orElse(false);

    if (runWasSuccessfullToday) {
      return;
    }

    IndexingState state =
        indexStatusService.loadStatus(CaseLawIndexSyncJob.CASELAW_STATUS_FILENAME);
    String lastProcessedChangelogFile = state.lastProcessedChangelogFile();
    if (Objects.isNull(lastProcessedChangelogFile)) {
      return;
    }

    List<String> filePaths = indexJob.getNewChangelogs(caselawbucket, lastProcessedChangelogFile);

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

    LocalDate now = LocalDate.now();
    List<UrlSet> urlSets = sitemapService.createUrlSets(changed, deleted);
    List<String> urlSetLocations = sitemapService.writeUrlSets(urlSets, now);

    if (!urlSetLocations.isEmpty()) {
      Sitemapindex index = sitemapService.createSitemapIndex(urlSetLocations);
      sitemapService.writeSitemapIndex(index, now);
    }

    portalBucket.save(LAST_SUCCESS_FILE, now.format(formatter));
  }
}
