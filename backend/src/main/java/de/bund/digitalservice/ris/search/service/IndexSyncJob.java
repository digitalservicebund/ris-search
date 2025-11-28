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

/**
 * Executes a job to synchronize the indexing by processing changelogs and updating the index state
 * accordingly. The job ensures that no concurrent indexing operations happen on the same index by
 * acquiring locks and applying relevant updates.
 *
 * <p>Implements the `Job` interface, and the primary operation is invoked via the `runJob` method.
 * The `runJobAsync` method provides asynchronous support for executing the job.
 *
 * <p>This class interacts with: - `IndexStatusService` to manage the lock status and update last
 * processed changelog information. - `ObjectStorage` to fetch changelog file information. -
 * `IndexService` to perform indexing operations based on the changelog or complete reindexes.
 */
public class IndexSyncJob implements Job {

  private static final Logger logger = LogManager.getLogger(IndexSyncJob.class);

  public static final String CHANGELOGS_PREFIX = "changelogs/";

  private final IndexStatusService indexStatusService;
  private final ObjectStorage changelogBucket;
  private final IndexService indexService;
  private final String statusFileName;

  /**
   * Constructs an IndexSyncJob with the specified services and status file *
   *
   * @param indexStatusService the service to manage index status
   * @param changelogBucket the object storage for changelog files
   * @param indexService the service to perform indexing operations
   * @param statusFileName the name of the status
   */
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
  public void runJobAsync() {
    runJob();
  }

  /**
   * Executes the index synchronization job, which involves loading the indexing state, acquiring a
   * lock, fetching and processing changes, and releasing the lock.
   *
   * <p>The method starts by fetching the current indexing status and attempts to acquire a lock for
   * the specified status file. If the lock cannot be acquired, the job ends successfully without
   * further action. If the lock is obtained, it proceeds to fetch and process changes (e.g., new or
   * updated changelog files). Once the processing is complete, the lock is released.
   *
   * <p>If an exception occurs during the operation, it is logged, and the method returns an error
   * code.
   *
   * @return the status of the job execution: {@code Job.ReturnCode.SUCCESS} if the job completes
   *     successfully {@code Job.ReturnCode.ERROR} if the job encounters an error during execution
   */
  public Job.ReturnCode runJob() {
    logger.info("Starting index sync job for {}", statusFileName);
    // load current state and lock if possible
    try {
      IndexingState state =
          indexStatusService.loadStatus(statusFileName).withStartTime(Instant.now().toString());
      boolean locked = indexStatusService.lockIndex(statusFileName, state);
      if (!locked) {
        return ReturnCode.SUCCESS;
      }

      fetchAndProcessChanges(state);
      indexStatusService.unlockIndex(statusFileName);
    } catch (ObjectStoreServiceException ex) {
      logger.error(ex);
      return ReturnCode.ERROR;
    }
    logger.info("Finished index sync job for {}", statusFileName);
    return ReturnCode.SUCCESS;
  }

  /**
   * Retrieves a list of new changelog file names from the specified object storage, filtered by
   * those that are newer than the specified last processed changelog.
   *
   * <p>The method fetches all file keys from the changelog bucket with the prefix defined by
   * `CHANGELOGS_PREFIX` and excludes the prefix itself from the results. It then filters out files
   * that are lexicographically older or equal to the `lastProcessedChangelog` and sorts the
   * remaining file names in ascending order.
   *
   * @param changelogBucket the object storage instance containing the changelog files
   * @param lastProcessedChangelog the filename of the last processed changelog; files with
   *     lexicographically greater names will be included
   * @return a sorted list of file names representing unprocessed changelog files
   */
  public List<String> getNewChangelogs(
      ObjectStorage changelogBucket, @NotNull String lastProcessedChangelog) {

    return changelogBucket.getAllKeysByPrefix(CHANGELOGS_PREFIX).stream()
        .filter(e -> !CHANGELOGS_PREFIX.equals(e))
        .filter(e -> e.compareTo(lastProcessedChangelog) > 0)
        .sorted()
        .toList();
  }

  /**
   * Fetches and processes changes from the changelog bucket based on the given indexing state.
   *
   * <p>If the `lastProcessedChangelogFile` in the provided state is null, a full reindexing
   * operation is performed starting from the `startTime` in the state. Otherwise, the method
   * identifies new changelogs that have not yet been processed, processes them, and updates the
   * status accordingly.
   *
   * <p>If no new changelogs are found, it logs that there are no changes since the last processed
   * changelog. The method also alerts if there is a mismatch in the number of files in the
   * changelog bucket and the number of indexed entities, provided there are no unprocessed
   * changelogs.
   *
   * @param state the current indexing state containing information about the last processed
   *     changelog and the start time for reindexing
   * @throws ObjectStoreServiceException if an error occurs during processing or interacting with
   *     the object store
   */
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
        importChangelogContent(changelogContent, state.startTime());
        indexStatusService.updateLastProcessedChangelog(statusFileName, fileName);
        logger.info("Processed changelog {}", fileName);
      }
    }
  }

  /**
   * Parses a single changelog file from the given object storage and converts its content to a
   * {@link Changelog} object.
   *
   * <p>The method fetches the file content from the object storage using the provided filename. If
   * no content is found, or if there is an error during parsing, the method logs an error and
   * returns null.
   *
   * @param changelogBucket The object storage instance to fetch the changelog file from.
   * @param filename The name of the file to be fetched and parsed as a changelog.
   * @return A {@link Changelog} object if parsing is successful, or null if the file could not be
   *     retrieved or parsed.
   * @throws ObjectStoreServiceException If an error occurs while accessing the object storage.
   */
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

  /**
   * Processes the provided changelog and updates the index based on its content.
   *
   * <p>If the `changeAll` flag in the changelog is set to true, all entities are reindexed starting
   * from the specified timestamp. Otherwise, only the specific changed and deleted entities in the
   * changelog are processed.
   *
   * <p>Throws an exception if there are duplicate identifiers in the `changed` and `deleted` lists
   * of the changelog.
   *
   * @param changelog The changelog containing identifiers to be processed and an indicator for full
   *     reindexing.
   * @param startTime The timestamp indicating the start time for reindexing, applicable when
   *     `changeAll` is true.
   * @throws ObjectStoreServiceException If an error occurs during processing or interacting with
   *     the object store.
   * @throws IllegalArgumentException If there are duplicate identifiers in the `changed` and
   *     `deleted` lists.
   */
  public void importChangelogContent(Changelog changelog, String startTime)
      throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      logger.info("Reindexing all");
      indexService.reindexAll(startTime);
    } else {
      if (!Collections.disjoint(changelog.getChanged(), changelog.getDeleted())) {
        throw new IllegalArgumentException("duplicate identifier in changed and deleted list");
      }
      indexService.indexChangelog(changelog);
    }
  }

  /**
   * Alerts when there is a mismatch between the number of files in the changelog bucket and the
   * number of indexed documents, only if there are no unprocessed changelogs.
   *
   * @param state the current indexing state containing information such as the last processed
   *     changelog file
   */
  public void alertOnNumberMismatch(IndexingState state) {
    if (state.lastProcessedChangelogFile() == null) {
      return;
    }
    List<String> unprocessedChangelogs =
        getNewChangelogs(changelogBucket, state.lastProcessedChangelogFile());
    if (unprocessedChangelogs.isEmpty()) {
      int numberOfFilesInBucket = indexService.getNumberOfIndexableDocumentsInBucket();
      int numberOfIndexedDocuments = indexService.getNumberOfIndexedEntities();
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
