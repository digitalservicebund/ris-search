package de.bund.digitalservice.ris.search.service;

import static de.bund.digitalservice.ris.search.service.BulkExportService.JOB_STATE_STORAGE_PREFIX;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Job to manage the lifecycle of zip snapshots. It creates snapshots a maximum of every 24 hours.
 * In case a document gets marked as deleted in its corresponding changelog service, the current
 * snapshot gets deleted and recreated, even outside of the 24 hour window.
 */
public class BulkExportJob implements Job {

  private final Logger logger = LogManager.getLogger(BulkExportJob.class);

  private final BulkExportService exportService;
  private final PortalBucket portalBucket;
  private final String documentType;
  private final ChangelogService<?> changelogService;

  /**
   * @param service BulkExportService to create zip snapshots
   * @param portalBucket portalBucket to store the state of the job
   * @param documentType the current documentType the job is configured for
   * @param changelogService the changelog service to manage the snapshot lifecycle
   */
  public BulkExportJob(
      BulkExportService service,
      PortalBucket portalBucket,
      String documentType,
      ChangelogService<?> changelogService) {
    this.exportService = service;
    this.portalBucket = portalBucket;
    this.documentType = documentType;
    this.changelogService = changelogService;
  }

  @Override
  public ReturnCode runJob() {
    Instant jobStartTime = Instant.now();
    try {

      var lastSuccess = portalBucket.getFileAsString(JOB_STATE_STORAGE_PREFIX + documentType);
      if (lastSuccess.isPresent()) {
        var lastSuccessInstant = Instant.parse(lastSuccess.get().trim());
        var changes = changelogService.getChangesBetween(lastSuccessInstant, jobStartTime);

        boolean noChanges =
            changes.getChanged().isEmpty()
                && changes.getDeleted().isEmpty()
                && !changes.isChangeAll();

        if (noChanges) {
          logger.info("No new changes detected. Keeping the snapshot");
          return ReturnCode.SUCCESS;
        }

        boolean deletionDetected =
            changes.isChangeAll()
                || changes.getDeleted().stream().anyMatch(d -> d.endsWith(".xml"));

        if (deletionDetected) {
          logger.info("Document deletion detected. Recreating snapshot");
          exportService.deleteArchives();
        } else {
          // If no deletions occurred, check if the last run was within the 24-hour cooldown period
          boolean runWasLessThanADayAgo =
              jobStartTime.isBefore(lastSuccessInstant.plus(1, ChronoUnit.DAYS));
          if (runWasLessThanADayAgo) {
            logger.info("Last snapshot is too recent");
            return ReturnCode.SUCCESS;
          }
        }
      }

      boolean success = exportService.updateLatestZip(jobStartTime);
      if (!success) {
        return ReturnCode.ERROR;
      }
      portalBucket.save(JOB_STATE_STORAGE_PREFIX + documentType, jobStartTime.toString());
      return ReturnCode.SUCCESS;
    } catch (ObjectStoreServiceException e) {
      logger.error("Bulk export job failed due to ObjectStoreServiceException.", e);
      return ReturnCode.ERROR;
    }
  }
}
