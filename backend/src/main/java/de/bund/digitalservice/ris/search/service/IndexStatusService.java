package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service for managing the indexing status file. */
@Service
public class IndexStatusService {

  private static final Logger logger = LogManager.getLogger(IndexStatusService.class);

  private final PortalBucket portalBucket;

  @Autowired
  public IndexStatusService(PortalBucket portalBucket) {
    this.portalBucket = portalBucket;
  }

  /**
   * Attempts to lock a specific index by updating its state with a lock time. If the index can be
   * locked as determined by the {@code canLock} method, its lock time is updated and the status is
   * saved.
   *
   * @param statusFileName the name of the file containing the indexing status
   * @param state the current indexing state, including information such as lock time and start time
   * @return {@code true} if the index was successfully locked; {@code false} otherwise
   */
  public boolean lockIndex(String statusFileName, IndexingState state) {
    if (canLock(statusFileName, state)) {
      state = state.withLockTime(state.startTime());
      saveStatus(statusFileName, state);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Determines if the current indexing process can acquire a lock based on the state of the system.
   * This method checks the lock time in the provided {@code IndexingState}. If no lock time is set,
   * the lock can be acquired. If a lock time is set but is older than 24 hours, a graceful reset is
   * allowed. Otherwise, the lock cannot be acquired since an import is currently in progress.
   *
   * @param statusFileName the name of the file containing the status of the indexing process
   * @param state the current indexing state, which includes information such as lock time and start
   *     time
   * @return {@code true} if the lock can be acquired; {@code false} otherwise
   */
  public boolean canLock(String statusFileName, IndexingState state) {
    if (state.lockTime() == null) {
      return true;
    } else if (Instant.parse(state.lockTime())
        .isBefore(state.startTimeInstant().minus(Duration.ofDays(1)))) {
      logger.error("Import has been locked for more than 24 hours. Attempting a graceful reset.");
      return true;
    } else {
      logger.warn(
          "Import already in progress for {}. Current import started at {}",
          statusFileName,
          state.lockTime());
      return false;
    }
  }

  public void unlockIndex(String statusFileName) throws ObjectStoreServiceException {
    saveStatus(statusFileName, loadStatus(statusFileName).withLockTime(null));
  }

  /**
   * Updates the last processed changelog file in the indexing status. This method loads the current
   * indexing status from the specified file, updates its last processed changelog file attribute,
   * and saves the updated status back.
   *
   * @param statusFileName the name of the file containing the indexing status
   * @param lastProcessedChangelogFile the name of the last processed changelog file to set
   * @throws ObjectStoreServiceException if an error occurs while loading or saving the status
   */
  public void updateLastProcessedChangelog(String statusFileName, String lastProcessedChangelogFile)
      throws ObjectStoreServiceException {
    saveStatus(
        statusFileName,
        loadStatus(statusFileName).withLastProcessedChangelogFile(lastProcessedChangelogFile));
  }

  /**
   * Loads the indexing status from the specified file. If the file does not exist or contains
   * invalid content, a new {@code IndexingState} instance is returned.
   *
   * @param statusFileName the name of the file containing the indexing status
   * @return the loaded {@code IndexingState}, or a new {@code IndexingState} if the file is missing
   *     or invalid
   * @throws ObjectStoreServiceException if an error occurs while accessing the file
   */
  public IndexingState loadStatus(String statusFileName) throws ObjectStoreServiceException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String content = portalBucket.getFileAsString(statusFileName).orElse(null);
      if (content == null) {
        return new IndexingState();
      }
      return mapper.readValue(content, IndexingState.class);
    } catch (JsonProcessingException e) {
      return new IndexingState();
    }
  }

  /**
   * Saves the indexing status to the specified file.
   *
   * @param statusFileName the name of the status file
   * @param status the indexing state to save
   */
  public void saveStatus(String statusFileName, IndexingState status) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String statusJson = mapper.writeValueAsString(status);
      portalBucket.save(statusFileName, statusJson);
    } catch (JsonProcessingException e) {
      logger.error("Error while saving status file", e);
    }
  }
}
