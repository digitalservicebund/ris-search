package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  private final IndexStatusService indexStatusService;
  private final ChangelogService changelogService;
  private final ObjectStorage changelogBucket;
  private final IndexService indexService;
  private final String statusFileName;

  /**
   * Constructs an IndexSyncJob with the specified services and status file *
   *
   * @param indexStatusService the service to manage index status
   * @param changelogService the object storage for changelog files
   * @param changelogBucket the object storage for changelog files
   * @param indexService the service to perform indexing operations
   * @param statusFileName the name of the status
   */
  public IndexSyncJob(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      ObjectStorage changelogBucket,
      IndexService indexService,
      String statusFileName) {
    this.indexStatusService = indexStatusService;
    this.changelogService = changelogService;
    this.indexService = indexService;
    this.statusFileName = statusFileName;
    this.changelogBucket = changelogBucket;
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
          statusFileName, ChangelogService.CHANGELOGS_PREFIX + state.startTime());
      alertOnNumberMismatch(state);
    } else {
      List<String> unprocessedChangelogs =
          changelogService
              .getNewChangelogsPaths(changelogBucket, state.lastProcessedChangelogFile())
              .stream()
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
      Changelog changelogContent = changelogService.parseOneChangelog(changelogBucket, fileName);
      if (changelogContent != null) {
        logger.info("Processing changelog {}", fileName);
        importChangelogContent(changelogContent, state.startTime());
        indexStatusService.updateLastProcessedChangelog(statusFileName, fileName);
        logger.info("Processed changelog {}", fileName);
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
        changelogService.getNewChangelogsPaths(changelogBucket, state.lastProcessedChangelogFile());
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
