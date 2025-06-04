package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;

public class IndexSyncJob {

  private static final Logger logger = LogManager.getLogger(IndexSyncJob.class);

  public static final String CHANGELOGS_PREFIX = "changelogs/";

  private final IndexStatusService indexStatusService;
  private final ObjectStorage changelogBucket;
  private final IndexService indexService;
  private final String statusFileName;

  public IndexSyncJob(
      IndexStatusService indexStatusService,
      ObjectStorage changelogBucket,
      IndexService indexService,
      String statusFileName) {
    this.indexStatusService = indexStatusService;
    this.changelogBucket = changelogBucket;
    this.indexService = indexService;
    this.statusFileName = statusFileName;
  }

  @Async
  public void runJobAsync() throws ObjectStoreServiceException {
    runJob();
  }

  public void runJob() throws ObjectStoreServiceException {
    logger.info("Starting index sync job for {}", statusFileName);
    // load current state and lock if possible
    IndexingState state =
        indexStatusService.loadStatus(statusFileName).withStartTime(Instant.now().toString());
    boolean locked = indexStatusService.lockIndex(statusFileName, state);
    if (!locked) {
      return;
    }

    fetchAndProcessChanges(state);
    indexStatusService.unlockIndex(statusFileName);
    logger.info("Finished index sync job for {}", statusFileName);
  }

  public List<String> getNewChangelogs(
      ObjectStorage changelogBucket, @NotNull String lastProcessedChangelog) {

    return changelogBucket.getAllKeysByPrefix(CHANGELOGS_PREFIX).stream()
        .filter(e -> !CHANGELOGS_PREFIX.equals(e))
        .filter(e -> e.compareTo(lastProcessedChangelog) > 0)
        .sorted()
        .toList();
  }

  public void fetchAndProcessChanges(IndexingState state) throws ObjectStoreServiceException {
    if (state.lastProcessedChangelogFile() == null) {
      // if status file or last success missing do a full reset
      logger.info("Reindexing all due to missing previous lastProcessedChangelogFile");
      indexService.reindexAll(state.startTime());
      indexStatusService.updateLastProcessedChangelog(
          statusFileName, CHANGELOGS_PREFIX + state.startTime());
      alertOnNumberMismatch(state);
    } else {
      List<String> unprocessedChangelogs =
          getNewChangelogs(changelogBucket, state.lastProcessedChangelogFile()).stream()
              .sorted()
              .toList();
      processChangelogs(state, unprocessedChangelogs);
      if (unprocessedChangelogs.isEmpty()) {
        logger.info("No new changelogs found since {}", state.lastProcessedChangelogFile());
      } else {
        alertOnNumberMismatch(state);
      }
    }
  }

  private void processChangelogs(IndexingState state, List<String> unprocessedChangelogs)
      throws ObjectStoreServiceException {
    for (String fileName : unprocessedChangelogs) {
      Changelog changelogContent = parseOneChangelog(changelogBucket, fileName);
      if (changelogContent != null) {
        logger.info("Processing changelog {}", fileName);
        importChangelogContent(changelogContent, state.startTime(), fileName);
        indexStatusService.updateLastProcessedChangelog(statusFileName, fileName);
        logger.info("Processed changelog {}", fileName);
      }
    }
  }

  public @Nullable Changelog parseOneChangelog(ObjectStorage changelogBucket, String filename)
      throws ObjectStoreServiceException {
    Optional<String> changelogContent = changelogBucket.getFileAsString(filename);
    if (changelogContent.isEmpty()) {
      logger.error("Changelog file {} could not be fetched during import.", filename);
      return null;
    } else {
      try {
        return new ObjectMapper().readValue(changelogContent.get(), Changelog.class);
      } catch (JsonProcessingException e) {
        logger.error("Error while parsing changelog file {}", filename, e);
        return null;
      }
    }
  }

  public void importChangelogContent(
      Changelog changelog, String startTime, String changelogFileName)
      throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      logger.info("Reindexing all");
      indexService.reindexAll(startTime);
    } else {
      if (!Collections.disjoint(changelog.getChanged(), changelog.getDeleted())) {
        throw new IllegalArgumentException("duplicate identifier in changed and deleted list");
      }
      indexService.indexChangelog(changelogFileName, changelog);
    }
  }

  public void alertOnNumberMismatch(IndexingState state) {
    if (state.lastProcessedChangelogFile() == null) {
      return;
    }
    List<String> unprocessedChangelogs =
        getNewChangelogs(changelogBucket, state.lastProcessedChangelogFile());
    if (unprocessedChangelogs.isEmpty()) {
      int numberOfFilesInBucket = indexService.getNumberOfFilesInBucket();
      int numberOfIndexedDocuments = indexService.getNumberOfIndexedDocuments();
      if (numberOfFilesInBucket != numberOfIndexedDocuments) {
        String indexServiceName = indexService.getClass().getSimpleName();
        logger.warn(
            "{} has {} files in bucket but {} indexed documents",
            indexServiceName,
            numberOfFilesInBucket,
            numberOfIndexedDocuments);
      }
    }
  }
}
