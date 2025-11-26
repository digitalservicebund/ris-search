package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Norms-specific ObjectStorage wrapper.
 *
 * <p>Provides a named ObjectStorage instance configured for the norms bucket. Delegates storage
 * operations to the provided ObjectStorageClient.
 */
@Component
public class NormsBucket extends ObjectStorage {

  public NormsBucket(@Qualifier("normS3Client") ObjectStorageClient normS3Client) {
    super(normS3Client, LogManager.getLogger(NormsBucket.class));
  }
}
