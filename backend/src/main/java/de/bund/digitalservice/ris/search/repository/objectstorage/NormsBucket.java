package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NormsBucket extends ObjectStorage {

  public NormsBucket(@Qualifier("normS3Client") ObjectStorageClient normS3Client) {
    super(normS3Client, LogManager.getLogger(NormsBucket.class));
  }
}
