package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

public class ImportService {

  private static final Logger logger = LogManager.getLogger(ImportService.class);

  private final IndexStatusService indexStatusService;
  private final ChangelogService changelogService;
  private final ObjectStorage changelogBucket;
  private final IndexService indexService;
  private final String statusFileName;

  public ImportService(
      IndexStatusService indexStatusService,
      ChangelogService changelogService,
      ObjectStorage changelogBucket,
      IndexService indexService,
      String statusFileName) {
    this.indexStatusService = indexStatusService;
    this.changelogService = changelogService;
    this.changelogBucket = changelogBucket;
    this.indexService = indexService;
    this.statusFileName = statusFileName;
  }

  @Async
  public void lockAndProcessChangelogsAsync() throws ObjectStoreServiceException {
    lockAndImportChangelogs();
  }

  public void lockAndImportChangelogs() throws ObjectStoreServiceException {
    // load current state and lock if possible
    IndexingState state =
        indexStatusService.loadStatus(statusFileName).withStartTime(Instant.now().toString());
    boolean locked = indexStatusService.lockIndex(statusFileName, state);
    if (!locked) {
      return;
    }

    if (state.lastSuccess() == null) {
      // if status file or last success missing do a full reset
      indexService.reindexAll(state.startTime());
      indexStatusService.updateLastSuccess(statusFileName, state.startTime());
    } else {
      // otherwise process files as normal
      importChangelogs(state);
    }

    alertOnNumberMismatch(state);
    indexStatusService.unlockIndex(statusFileName);
  }

  public void importChangelogs(IndexingState state) throws ObjectStoreServiceException {
    List<String> unprocessedChangelogs =
        changelogService.getNewChangelogsSinceInstant(changelogBucket, state.lastSuccessInstant());

    for (String fileName : unprocessedChangelogs) {
      state = state.withCurrentChangelogFile(fileName);
      Changelog changelogContent = changelogService.parseOneChangelog(changelogBucket, fileName);
      if (changelogContent != null) {
        importChangelogContent(changelogContent, state);
        Optional<Instant> changelogTimestamp = changelogService.getInstantFromChangelog(fileName);
        if (changelogTimestamp.isPresent()) {
          indexStatusService.updateLastSuccess(statusFileName, changelogTimestamp.get().toString());
        }
      }
    }
  }

  public void importChangelogContent(Changelog changelog, IndexingState state)
      throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      logger.info("Reindexing all");
      indexService.reindexAll(state.startTime());
    } else {
      if (!Collections.disjoint(changelog.getChanged(), changelog.getDeleted())) {
        throw new IllegalArgumentException("duplicate identifier in changed and deleted list");
      }
      indexService.indexChangelog(state.currentChangelogFile(), changelog);
    }
  }

  public void alertOnNumberMismatch(IndexingState state) {
    List<String> unprocessedChangelogs =
        changelogService.getNewChangelogsSinceInstant(changelogBucket, state.lastSuccessInstant());
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
  }
}
