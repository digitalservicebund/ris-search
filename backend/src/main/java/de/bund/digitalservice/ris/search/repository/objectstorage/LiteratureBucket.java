package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LiteratureBucket extends ObjectStorage {

  public LiteratureBucket(@Qualifier("literatureS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(LiteratureBucket.class));
  }
}
