package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.exception.FatalEcliSitemapJobException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.BulkChangelogParser;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.Job;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Job for generating ECLI sitemaps. */
@Service
public class EcliSitemapJob implements Job {

  EcliSitemapWriter sitemapWriter;

  PortalBucket portalBucket;

  CaseLawBucket caselawbucket;

  CaseLawIndexSyncJob indexJob;

  EcliCrawlerDocumentService ecliCrawlerDocumentService;
  private final LocalDate today = LocalDate.now();

  private static final Logger logger = LogManager.getLogger(EcliSitemapJob.class);
  public static final String LAST_PROCESSED_CHANGELOG = "eclicrawler/last_processed_changelog";
  private final String apiUrl;

  /**
   * Constructor for EcliSitemapJob.
   *
   * @param service EcliSitemapWriter service for writing sitemaps.
   * @param portalBucket PortalBucket for storing portal-related files.
   * @param caselawbucket CaseLawBucket for accessing case law data.
   * @param indexJob CaseLawIndexSyncJob for synchronizing case law index.
   * @param ecliCrawlerDocumentService EcliCrawlerDocumentService for handling ECLI crawler
   *     documents.
   * @param frontEndUrl Front-end URL from application properties.
   */
  public EcliSitemapJob(
      EcliSitemapWriter service,
      PortalBucket portalBucket,
      CaseLawBucket caselawbucket,
      CaseLawIndexSyncJob indexJob,
      EcliCrawlerDocumentService ecliCrawlerDocumentService,
      @Value("${server.front-end-url}") String frontEndUrl) {
    this.sitemapWriter = service;
    this.portalBucket = portalBucket;
    this.caselawbucket = caselawbucket;
    this.indexJob = indexJob;
    this.ecliCrawlerDocumentService = ecliCrawlerDocumentService;
    this.apiUrl = frontEndUrl + "v1/eclicrawler/";
  }

  /**
   * Runs the ECLI sitemap job.
   *
   * @return ReturnCode indicating the result of the job execution.
   */
  public ReturnCode runJob() {
    if (!sitemapWriter.getSitemapFilesPathsForDay(today).isEmpty()) {
      logger.warn("day partition for ecli sitemap run already created");
      return ReturnCode.SUCCESS;
    }

    boolean isInitialRun = portalBucket.getAllKeysByPrefix(LAST_PROCESSED_CHANGELOG).isEmpty();
    try {
      if (isInitialRun) {
        logger.info("initial run, publish all");
        sitemapWriter.writeRobotsTxt();
        String newestChangelog = indexJob.getNewChangelogs(caselawbucket, "0").getLast();
        ecliCrawlerDocumentService.writeFullDiff(apiUrl, today);
        portalBucket.save(LAST_PROCESSED_CHANGELOG, newestChangelog);
      } else {
        var lastProcessed = getLastProcessedChangelog();
        List<String> changelogPaths = indexJob.getNewChangelogs(caselawbucket, lastProcessed);
        if (changelogPaths.isEmpty()) {
          logger.info("no new changelogs to parse");
          return ReturnCode.SUCCESS;
        }
        var changelogs = getNewChangelogs(changelogPaths);
        if (BulkChangelogParser.containsChangeAll(changelogs)) {
          ecliCrawlerDocumentService.writeFullDiff(apiUrl, today);
        } else {
          ecliCrawlerDocumentService.writeFromChangelog(
              apiUrl, today, BulkChangelogParser.mergeChangelogs(changelogs));
        }
        portalBucket.save(LAST_PROCESSED_CHANGELOG, changelogPaths.getLast());
      }
    } catch (RuntimeException e) {
      logger.error(e.getMessage());
      return ReturnCode.ERROR;
    }

    return ReturnCode.SUCCESS;
  }

  private String getLastProcessedChangelog() {
    try {
      return portalBucket
          .getFileAsString(LAST_PROCESSED_CHANGELOG)
          .orElseThrow(() -> new FatalEcliSitemapJobException("unable to parse status file"));
    } catch (ObjectStoreServiceException e) {
      throw new FatalEcliSitemapJobException(e.getMessage());
    }
  }

  private List<Changelog> getNewChangelogs(List<String> changelogPaths) {

    return changelogPaths.stream()
        .map(
            path -> {
              try {
                return this.indexJob.parseOneChangelog(caselawbucket, path);
              } catch (ObjectStoreServiceException e) {
                throw new FatalEcliSitemapJobException(e.getMessage());
              }
            })
        .filter(changelog -> !Objects.isNull(changelog))
        .toList();
  }
}
