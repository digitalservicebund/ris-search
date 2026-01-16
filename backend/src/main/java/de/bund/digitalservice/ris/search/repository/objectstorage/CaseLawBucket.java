package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * CaseLaw-specific ObjectStorage wrapper.
 *
 * <p>Provides a named ObjectStorage instance configured for the case law bucket. Delegates storage
 * operations to the provided ObjectStorageClient.
 */
@Component
@Profile({"default", "staging", "uat", "test", "prototype", "production"})
public class CaseLawBucket extends ObjectStorage {

  public CaseLawBucket(@Qualifier("caseLawS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(CaseLawBucket.class));
  }
}
