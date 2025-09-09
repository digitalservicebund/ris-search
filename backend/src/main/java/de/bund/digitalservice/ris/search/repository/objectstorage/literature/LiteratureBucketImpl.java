package de.bund.digitalservice.ris.search.repository.objectstorage.literature;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import de.bund.digitalservice.ris.search.repository.objectstorage.S3ObjectStorage;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"default", "staging", "test"})
public class LiteratureBucketImpl extends S3ObjectStorage implements LiteratureBucket {

  public LiteratureBucketImpl(@Qualifier("literatureS3Client") ObjectStorageClient s3Client) {
    super(s3Client, LogManager.getLogger(LiteratureBucketImpl.class));
  }
}
