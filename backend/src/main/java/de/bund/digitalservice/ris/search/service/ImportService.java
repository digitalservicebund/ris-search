package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

public class ImportService {

  private static final Logger logger = LogManager.getLogger(ImportService.class);

  public static final String CHANGELOG = "changelogs/";

  private final IndexStatusService indexStatusService;
  private final ObjectStorage changelogBucket;
  private final IndexService indexService;
  private final String statusFileName;

  public ImportService(
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

  public List<String> getNewChangelogsSinceInstant(
      ObjectStorage changelogBucket, Instant threshold) {

    return changelogBucket.getAllKeysByPrefix(CHANGELOG).stream()
        .filter(e -> !CHANGELOG.equals(e))
        .filter(e -> changelogIsNewerThanThreshold(e, threshold))
        .sorted()
        .toList();
  }

  private boolean changelogIsNewerThanThreshold(String filename, Instant threshold) {
    return getInstantFromChangelog(filename).map(time -> time.isAfter(threshold)).orElse(false);
  }

  public Optional<Instant> getInstantFromChangelog(String filename) {
    try {
      String timeString = filename.substring(filename.indexOf("/") + 1, filename.indexOf("Z") + 1);
      Instant instant = Instant.parse(timeString);
      return Optional.of(instant);
    } catch (StringIndexOutOfBoundsException | NullPointerException | DateTimeParseException e) {
      logger.error("unable to parse invalid changelog timestamp {}", filename);
      return Optional.empty();
    }
  }

  public void importChangelogs(IndexingState state) throws ObjectStoreServiceException {
    List<String> unprocessedChangelogs =
        getNewChangelogsSinceInstant(changelogBucket, state.lastSuccessInstant());

    for (String fileName : unprocessedChangelogs) {
      state = state.withCurrentChangelogFile(fileName);
      Changelog changelogContent = parseOneChangelog(changelogBucket, fileName);
      if (changelogContent != null) {
        importChangelogContent(changelogContent, state);
        Optional<Instant> changelogTimestamp = getInstantFromChangelog(fileName);
        if (changelogTimestamp.isPresent()) {
          indexStatusService.updateLastSuccess(statusFileName, changelogTimestamp.get().toString());
        }
      }
    }
  }

  public Changelog parseOneChangelog(ObjectStorage changelogBucket, String filename)
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
        getNewChangelogsSinceInstant(changelogBucket, state.lastSuccessInstant());
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
