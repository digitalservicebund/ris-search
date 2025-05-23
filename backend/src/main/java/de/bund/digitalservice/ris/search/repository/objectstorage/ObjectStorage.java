package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

public class ObjectStorage {

  public static final int MAXIMUM_CALL_ATTEMPTS = 3;
  private final Logger logger;
  private final ObjectStorageClient client;

  public ObjectStorage(ObjectStorageClient client, Logger logger) {
    this.client = client;
    this.logger = logger;
  }

  public List<String> getAllKeys() {
    return getAllKeysByPrefix("");
  }

  public List<String> getAllKeysByPrefix(String path) {
    return client.listKeysByPrefix(path);
  }

  public Optional<String> getFileAsString(String filename) throws ObjectStoreServiceException {
    Optional<byte[]> s3Response = get(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  public Optional<byte[]> get(String objectKey) throws ObjectStoreServiceException {
    for (int i = 0; i < MAXIMUM_CALL_ATTEMPTS; i++) {
      try {
        final var response = getStream(objectKey);
        return Optional.of(response.readAllBytes());
      } catch (NoSuchKeyException e) {
        logger.warn(String.format("Object key %s does not exist", objectKey));
        return Optional.empty();
      } catch (IOException | AwsServiceException | SdkClientException e) {
        logger.warn(
            "Object storage encountered an issue while trying to get object {}."
                + " Attempt {} will try again.",
            objectKey,
            i,
            e);
      }
    }
    throw new ObjectStoreServiceException(
        "Object storage encountered an issue. All retries failed.");
  }

  public FilterInputStream getStream(String objectKey) throws NoSuchKeyException {
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

  public long putStream(String objectKey, InputStream inputStream) throws IOException {
    return client.putStream(objectKey, inputStream);
  }
}
