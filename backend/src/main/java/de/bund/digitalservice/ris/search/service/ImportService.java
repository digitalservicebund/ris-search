package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.IndexingState;
import de.bund.digitalservice.ris.search.repository.objectstorage.PersistedIndexingState;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

  public static final String CASELAW_STATUS_FILENAME = "caselaw_status.json";
  public static final String NORM_STATUS_FILENAME = "norm_status.json";

  @Autowired
  public ImportService(IndexStatusService indexStatusService, ChangelogService changelogService) {
    this.indexStatusService = indexStatusService;
    this.changelogService = changelogService;
  }

  @Async
  public void lockAndProcessChangelogsAsync(IndexingState state) throws ObjectStoreException {
    lockAndImportChangelogs(state);
  }

  public void lockAndImportChangelogs(IndexingState state) throws ObjectStoreServiceException {
    // load current state and lock if possible
    PersistedIndexingState persistedState =
        indexStatusService.loadStatus(state.getStatusFileName());
    state.setPersistedIndexingState(persistedState);
    boolean locked = indexStatusService.lockIndex(state);
    if (!locked) {
      return;
    }

    if (persistedState.lastSuccess() == null) {
      // if status file or last success missing do a full reset
      state.getIndexService().reindexAll(state.getStartTime().toString());
      indexStatusService.updateLastSuccess(state.getStatusFileName(), state.getStartTime());
    } else {
      // otherwise process files as normal
      importChangelogs(state);
    }

    alertOnNumberMismatch(state);
    indexStatusService.unlockIndex(state.getStatusFileName());
  }

  public void importChangelogs(IndexingState state) throws ObjectStoreServiceException {
    PersistedIndexingState persistedState = state.getPersistedIndexingState();
    List<String> unprocessedChangelogs =
        changelogService.getNewChangelogsSinceInstant(
            state.getChangelogBucket(), persistedState.lastSuccessInstant());

    for (String fileName : unprocessedChangelogs) {
      persistedState = persistedState.withCurrentChangelogFile(fileName);
      Changelog changelogContent =
          changelogService.parseOneChangelog(state.getChangelogBucket(), fileName);
      if (changelogContent != null) {
        importChangelogContent(changelogContent, state);
        Optional<Instant> changelogTimestamp = changelogService.getInstantFromChangelog(fileName);
        if (changelogTimestamp.isPresent()) {
          indexStatusService.updateLastSuccess(state.getStatusFileName(), changelogTimestamp.get());
        }
      }
    }
  }

  public void importChangelogContent(Changelog changelog, IndexingState state)
      throws ObjectStoreServiceException {
    if (changelog.isChangeAll()) {
      logger.info("Reindexing all");
      state.getIndexService().reindexAll(state.getStartTime().toString());
    } else {
      if (!Collections.disjoint(changelog.getChanged(), changelog.getDeleted())) {
        throw new IllegalArgumentException("duplicate identifier in changed and deleted list");
      }
      state
          .getIndexService()
          .indexChangelog(state.getPersistedIndexingState().currentChangelogFile(), changelog);
    }
  }

  public void alertOnNumberMismatch(IndexingState state) {
    List<String> unprocessedChangelogs =
        changelogService.getNewChangelogsSinceInstant(
            state.getChangelogBucket(), state.getPersistedIndexingState().lastSuccessInstant());
    if (unprocessedChangelogs.isEmpty()) {
      int numberOfFilesInBucket = state.getIndexService().getNumberOfFilesInBucket();
      int numberOfIndexedDocuments = state.getIndexService().getNumberOfIndexedDocuments();
      if (numberOfFilesInBucket != numberOfIndexedDocuments) {
        String indexServiceName = state.getIndexService().getClass().getSimpleName();
        logger.error(
            "{} has {} files in bucket but {} indexed documents",
            indexServiceName,
            numberOfFilesInBucket,
            numberOfIndexedDocuments);
      }
    }
  }
}
