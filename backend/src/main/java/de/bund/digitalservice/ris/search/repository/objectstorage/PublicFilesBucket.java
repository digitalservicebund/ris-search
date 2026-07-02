package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Public bucket ObjectStorage wrapper.
 *
 * <p>Provides a named ObjectStorage instance configured for the public bucket. Delegates all
 * operations to the supplied ObjectStorageClient.
 */
@Component
public class PublicFilesBucket extends ObjectStorage {

  public PublicFilesBucket(@Qualifier("publicFilesS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(PublicFilesBucket.class), "");
  }
}
