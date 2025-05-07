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

@Service
public class IndexStatusService {

  private static final Logger logger = LogManager.getLogger(IndexStatusService.class);

  private final PortalBucket portalBucket;

  @Autowired
  public IndexStatusService(PortalBucket portalBucket) {
    this.portalBucket = portalBucket;
  }

  public boolean lockIndex(String statusFileName, IndexingState state) {
    if (canLock(statusFileName, state)) {
      state = state.withLockTime(state.startTime());
      saveStatus(statusFileName, state);
      return true;
    } else {
      return false;
    }
  }

  public boolean canLock(String StatusFileName, IndexingState state) {
    if (state.lockTime() == null) {
      return true;
    } else if (Instant.parse(state.lockTime())
        .isBefore(state.startTimeInstant().minus(Duration.ofDays(1)))) {
      logger.error("Import has been locked for more than 24 hours. Attempting a graceful reset.");
      return true;
    } else {
      logger.warn(
          "Import already in progress for {}. Current import started at {}",
          StatusFileName,
          state.lockTime());
      return false;
    }
  }

  public void unlockIndex(String statusFileName) throws ObjectStoreServiceException {
    saveStatus(statusFileName, loadStatus(statusFileName).withLockTime(null));
  }

  public void updateLastSuccess(String statusFileName, String lastSuccess)
      throws ObjectStoreServiceException {
    saveStatus(statusFileName, loadStatus(statusFileName).withLastSuccess(lastSuccess));
  }

  public IndexingState loadStatus(String statusFileName) throws ObjectStoreServiceException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String content = portalBucket.getFileAsString(statusFileName).orElse(null);
      if (content == null) {
        return new IndexingState();
      }
      return mapper.readValue(content, IndexingState.class);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

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
