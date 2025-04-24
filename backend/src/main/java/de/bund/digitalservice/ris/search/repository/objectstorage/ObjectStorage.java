package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

public interface ObjectStorage {

  public List<String> getAllFilenames();

  public List<String> getAllFilenamesByPath(String path);

  public Logger getLogger();

  default Optional<byte[]> get(String objectKey) {
    try {
      return checkedGet(objectKey);
    } catch (RetryableObjectStoreException e) {
      this.getLogger().error("AWS S3 encountered an issue.", e);
    }
    return Optional.empty();
  }

  default Optional<String> getFileAsString(String filename) throws RetryableObjectStoreException {
    Optional<byte[]> s3Response = checkedGet(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }
  ;

  default Optional<byte[]> checkedGet(String objectKey) throws RetryableObjectStoreException {
    try {
      final var response = getStream(objectKey);
      return Optional.of(response.readAllBytes());
    } catch (FileNotFoundException e) {
      this.getLogger().warn(String.format("Object key %s does not exist", objectKey));
      return Optional.empty();
    } catch (IOException | AwsServiceException | SdkClientException e) {
      throw new RetryableObjectStoreException(
          String.format("S3 has encountered a problem while trying to get object %s.", objectKey),
          e);
    }
  }
  ;

  public FilterInputStream getStream(String objectKey) throws FileNotFoundException;

  public void delete(String fileName);

  public void save(String fileName, String fileContent);

  public void close();
}
