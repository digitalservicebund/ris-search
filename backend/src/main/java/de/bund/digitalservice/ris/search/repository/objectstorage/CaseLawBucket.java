package de.bund.digitalservice.ris.search.repository.objectstorage;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"default", "staging", "uat", "test", "prototype"})
public class CaseLawBucket extends S3ObjectStorage {

  public CaseLawBucket(@Qualifier("caseLawS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(CaseLawBucket.class));
  }
}
