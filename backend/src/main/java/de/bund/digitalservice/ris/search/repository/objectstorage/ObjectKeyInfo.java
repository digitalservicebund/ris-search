package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.time.Instant;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * Represents an object key together with its last modified timestamp.
 *
 * <p>This record is used to carry key metadata returned from object storage listings.
 *
 * @param key the object key (path) in the storage
 * @param lastModified the instant the object was last modified
 */
public record ObjectKeyInfo(String key, Instant lastModified) {
  /**
   * Create an ObjectKeyInfo from an AWS S3Object.
   *
   * @param s3Object the S3Object to convert
   * @return a new ObjectKeyInfo containing the S3 object's key and last modified instant
   */
  public static ObjectKeyInfo fromS3Object(S3Object s3Object) {
    return new ObjectKeyInfo(s3Object.key(), s3Object.lastModified());
  }
}
