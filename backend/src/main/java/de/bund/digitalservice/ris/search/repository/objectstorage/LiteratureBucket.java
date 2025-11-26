package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Literature-specific ObjectStorage wrapper.
 *
 * <p>Provides a named ObjectStorage instance configured for the literature bucket. Delegates
 * storage operations to the provided ObjectStorageClient.
 */
@Component
public class LiteratureBucket extends ObjectStorage {

  /**
   * Create a LiteratureBucket wrapper using the provided ObjectStorageClient.
   *
   * @param s3Client the client instance bound to the literature bucket
   */
  public LiteratureBucket(@Qualifier("literatureS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(LiteratureBucket.class));
  }
}
