package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
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

  /**
   * Stores a lockfile to ensure only one index job is running at once.
   *
   * @param lockFileName String name of the lockfile
   * @param currentTime Instant startTime that is supposed to be persisted with the lockfile
   * @return returns if a new lock file was written
   */
  public boolean lockIndex(String lockFileName, Instant currentTime) {
    try {
      String lockedAt = portalBucket.getFileAsString(lockFileName).orElse(null);
      if (canLock(lockedAt, lockFileName, currentTime)) {
        portalBucket.save(lockFileName, currentTime.toString());
        return true;
      } else {
        return false;
      }
    } catch (ObjectStoreServiceException e) {
      logger.error("AWS S3 encountered an issue.", e);
      return false;
    }
  }

  private boolean canLock(String lockedAt, String statusFileName, Instant currentTime) {
    if (lockedAt == null) {
      return true;
    } else if (Instant.parse(lockedAt).isBefore(currentTime.minus(Duration.ofDays(1)))) {
      logger.error("Import has been locked for more than 24 hours. Attempting a graceful reset.");
      return true;
    } else {
      logger.warn(
          "Import already in progress for {}. Current import started at {}",
          statusFileName,
          lockedAt);
      return false;
    }
  }

  /**
   * release the lock of a specific file
   *
   * @param lockFileName String reference of the lockfile
   */
  public void unlockIndex(String lockFileName) {
    portalBucket.delete(lockFileName);
  }

  public Instant getLastSuccess(String fileName) throws ObjectStoreServiceException {
    String lastSuccess = portalBucket.getFileAsString(fileName).orElse(null);
    if (lastSuccess == null) {
      logger.error("Status file missing.");
      return null;
    } else {
      try {
        return Instant.parse(lastSuccess);
      } catch (DateTimeParseException e) {
        logger.error("Status file has an invalid format.");
        return null;
      }
    }
  }

  /**
   * stores the time of the last successful run
   *
   * @param lastSuccessFilename String
   * @param lastSuccess Instant
   */
  public void updateLastSuccess(String lastSuccessFilename, Instant lastSuccess) {
    portalBucket.save(lastSuccessFilename, lastSuccess.toString());
  }
}
