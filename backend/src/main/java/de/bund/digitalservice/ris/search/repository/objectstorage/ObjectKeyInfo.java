package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.time.Instant;
import software.amazon.awssdk.services.s3.model.S3Object;

public record ObjectKeyInfo(String key, Instant lastModified) {
  public static ObjectKeyInfo fromS3Object(S3Object s3Object) {
    return new ObjectKeyInfo(s3Object.key(), s3Object.lastModified());
  }
}
