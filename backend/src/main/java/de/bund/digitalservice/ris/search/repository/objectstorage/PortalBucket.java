package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.caselawhandover.shared.S3Bucket;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public class PortalBucket extends S3Bucket {

  public PortalBucket(
      @Qualifier("portalS3Client") S3Client s3Client,
      @Value("${s3.file-storage.portal.bucket-name}") String bucketName) {
    super(s3Client, bucketName, LogManager.getLogger(PortalBucket.class));
  }
}
