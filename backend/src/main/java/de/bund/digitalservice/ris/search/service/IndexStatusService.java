package de.bund.digitalservice.ris.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreException;
import de.bund.digitalservice.ris.search.repository.objectstorage.IndexingState;
import de.bund.digitalservice.ris.search.repository.objectstorage.PersistedIndexingState;
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

  public boolean lockIndex(IndexingState state) {
    if (canLock(state)) {
      PersistedIndexingState persistedState =
          state.getPersistedIndexingState().withLockTime(state.getStartTime().toString());
      state.setPersistedIndexingState(persistedState);
      saveStatus(state.getStatusFileName(), persistedState);
      return true;
    } else {
      return false;
    }
  }

  public boolean canLock(IndexingState state) {
    PersistedIndexingState persistedState = state.getPersistedIndexingState();
    if (persistedState.lockTime() == null) {
      return true;
    } else if (Instant.parse(persistedState.lockTime())
        .isBefore(state.getStartTime().minus(Duration.ofDays(1)))) {
      logger.error("Import has been locked for more than 24 hours. Attempting a graceful reset.");
      return true;
    } else {
      logger.warn(
          "Import already in progress for {}. Current import started at {}",
          state.getStatusFileName(),
          persistedState.lockTime());
      return false;
    }
  }

  public void unlockIndex(String statusFileName) throws ObjectStoreException {
    saveStatus(statusFileName, loadStatus(statusFileName).withLockTime(null));
  }

  public void updateLastSuccess(String statusFileName, Instant lastSuccess)
      throws ObjectStoreException {
    saveStatus(statusFileName, loadStatus(statusFileName).withLastSuccess(lastSuccess.toString()));
  }

  public PersistedIndexingState loadStatus(String statusFileName) throws ObjectStoreException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String content = portalBucket.getFileAsString(statusFileName).orElse(null);
      if (content == null) {
        return null;
      }
      return mapper.readValue(content, PersistedIndexingState.class);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  public void saveStatus(String statusFileName, PersistedIndexingState status) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String statusJson = mapper.writeValueAsString(status);
      portalBucket.save(statusFileName, statusJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
