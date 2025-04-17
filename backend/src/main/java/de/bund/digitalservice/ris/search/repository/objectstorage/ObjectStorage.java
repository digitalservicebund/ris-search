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

public class ObjectStorage {

  private final Logger logger;
  private final ObjectStorageClient client;

  public ObjectStorage(ObjectStorageClient client, Logger logger) {
    this.client = client;
    this.logger = logger;
  }

  public List<String> getAllFilenames() {
    return getAllFilenamesByPath("");
  }

  public List<String> getAllFilenamesByPath(String path) {
    return client.getAllFilenamesByPath(path);
  }

  public Optional<byte[]> get(String objectKey) {
    try {
      return checkedGet(objectKey);
    } catch (RetryableObjectStoreException e) {
      logger.error("AWS S3 encountered an issue.", e);
    }
    return Optional.empty();
  }

  public Optional<String> getFileAsString(String filename) throws RetryableObjectStoreException {
    Optional<byte[]> s3Response = checkedGet(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  public Optional<byte[]> checkedGet(String objectKey) throws RetryableObjectStoreException {
    try {
      final var response = getStream(objectKey);
      return Optional.of(response.readAllBytes());
    } catch (FileNotFoundException e) {
      logger.warn(String.format("Object key %s does not exist", objectKey));
      return Optional.empty();
    } catch (IOException | AwsServiceException | SdkClientException e) {
      throw new RetryableObjectStoreException(
          String.format("S3 has encountered a problem while trying to get object %s.", objectKey),
          e);
    }
  }

  public FilterInputStream getStream(String objectKey) throws FileNotFoundException {
    return client.getStream(objectKey);
  }

  public void delete(String fileName) {
    client.delete(fileName);
  }

  public void save(String fileName, String fileContent) {
    client.save(fileName, fileContent);
  }

  public void close() {
    client.close();
  }
}
