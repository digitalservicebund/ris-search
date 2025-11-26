package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * AdministrativeDirective-specific ObjectStorage wrapper.
 *
 * <p>Provides a named ObjectStorage instance configured for the administrative directive bucket.
 * Delegates storage operations to the provided ObjectStorageClient.
 */
@Component
public class AdministrativeDirectiveBucket extends ObjectStorage {

  public AdministrativeDirectiveBucket(
      @Qualifier("administrativeDirectiveS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(AdministrativeDirectiveBucket.class));
  }
}
