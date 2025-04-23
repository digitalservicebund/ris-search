package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.caselawhandover.shared.S3Bucket;
import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ImportService {

  private final IndexStatusService indexStatusService;
  private static final Logger logger = LogManager.getLogger(ImportService.class);
  private final ChangelogService changelogService;

  public static final String NORM_LAST_SUCCESS_FILENAME = "norm_last_success.txt";
  public static final String NORM_LOCK_FILENAME = "norm_lock.txt";

  public static final String CASELAW_LAST_SUCCESS_FILENAME = "caselaw_last_success.txt";
  public static final String CASELAW_LOCK_FILENAME = "caselaw_lock.txt";

  @Autowired
  public ImportService(IndexStatusService indexStatusService, ChangelogService changelogService) {
    this.indexStatusService = indexStatusService;
    this.changelogService = changelogService;
  }

  @Async
  public void lockAndProcessChangelogsAsync(
      IndexService importService, String lockfile, String statusFile, S3Bucket changelogBucket) {
    lockAndImportChangelogs(importService, lockfile, statusFile, changelogBucket);
  }

  public void lockAndImportChangelogs(
      IndexService indexService,
      String lockfile,
      String lastSuccessFilename,
      S3Bucket changelogBucket) {
    Instant startTime = Instant.now();
    boolean locked = indexStatusService.lockIndex(lockfile, startTime);
    if (locked) {
      try {
        Instant lastSuccess = indexStatusService.getLastSuccess(lastSuccessFilename);
        if (lastSuccess == null) {
          indexService.reindexAll(startTime.toString());
          indexStatusService.updateLastSuccess(lastSuccessFilename, startTime);
          alertOnNumberMismatch(
              indexStatusService, indexService, lastSuccessFilename, changelogBucket);
        } else {
          importChangelogs(indexService, changelogBucket, lastSuccess, lastSuccessFilename);
        }
        indexStatusService.unlockIndex(lockfile);
      } catch (RetryableObjectStoreException e) {
        logger.error("Import process failed due to a retryable error. Removing lock", e);
        indexStatusService.unlockIndex(lockfile);
      }
    }
  }

  public void importChangelogs(
      IndexService indexService,
      S3Bucket changelogBucket,
      Instant lastSuccess,
      String lastSuccessFilename)
      throws RetryableObjectStoreException {
    List<String> unprocessedChangelogs =
        changelogService.getNewChangelogsSinceInstant(changelogBucket, lastSuccess);

    for (String fileName : unprocessedChangelogs) {
      Changelog changelogContent = changelogService.parseOneChangelog(changelogBucket, fileName);
      if (changelogContent != null) {
        importChangelogContent(fileName, changelogContent, indexService, Instant.now());
        changelogService
            .getInstantFromChangelog(fileName)
            .ifPresent(
                updatedLastSuccess ->
                    indexStatusService.updateLastSuccess(lastSuccessFilename, updatedLastSuccess));
      }
    }

    if (!unprocessedChangelogs.isEmpty()) {
      // do not check number mismatch if nothing was processed
      alertOnNumberMismatch(indexStatusService, indexService, lastSuccessFilename, changelogBucket);
    }
  }

  public void importChangelogContent(
      String changelogKey, Changelog changelog, IndexService service, Instant startTime)
      throws RetryableObjectStoreException {
    if (changelog.isChangeAll()) {
      logger.info("Reindexing all");
      service.reindexAll(startTime.toString());
    } else {
      if (!Collections.disjoint(changelog.getChanged(), changelog.getDeleted())) {
        throw new IllegalArgumentException("duplicate identifier in changed and deleted list");
      }
      service.indexChangelog(changelogKey, changelog);
    }
  }

  public void alertOnNumberMismatch(
      IndexStatusService indexStatusService,
      IndexService indexService,
      String lastSuccessFilename,
      S3Bucket changelogBucket) {
    try {
      Instant lastSuccess = indexStatusService.getLastSuccess(lastSuccessFilename);
      List<String> unprocessedChangelogs =
          changelogService.getNewChangelogsSinceInstant(changelogBucket, lastSuccess);
      if (unprocessedChangelogs.isEmpty()) {
        int numberOfFilesInBucket = indexService.getNumberOfFilesInBucket();
        int numberOfIndexedDocuments = indexService.getNumberOfIndexedDocuments();
        if (numberOfFilesInBucket != numberOfIndexedDocuments) {
          String indexServiceName = indexService.getClass().getSimpleName();
          logger.error(
              "{} has {} files in bucket but {} indexed documents",
              indexServiceName,
              numberOfFilesInBucket,
              numberOfIndexedDocuments);
        }
      }
    } catch (RetryableObjectStoreException e) {
      logger.error("Import process failed due to a retryable error.", e);
    }
  }
}
