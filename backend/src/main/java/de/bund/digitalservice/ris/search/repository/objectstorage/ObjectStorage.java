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

/**
 * Provides an abstraction for storing and retrieving objects from an object storage system.
 *
 * <p>This class utilizes an {@code ObjectStorageClient} to perform operations like saving,
 * retrieving, and deleting objects within the storage. It also manages retries for certain
 * operations, and supports operations such as listing keys and retrieving objects as strings or
 * byte streams.
 */
public class ObjectStorage {

  public static final int MAXIMUM_CALL_ATTEMPTS = 3;
  private final Logger logger;
  private final ObjectStorageClient client;

  /**
   * Constructs an ObjectStorage instance.
   *
   * @param client the object storage client used for operations such as saving, retrieving, and
   *     deleting objects
   * @param logger the logger used for logging activities and errors within the object storage
   */
  public ObjectStorage(ObjectStorageClient client, Logger logger) {
    this.client = client;
    this.logger = logger;
  }

  /**
   * Retrieves a list of all keys stored in the object storage.
   *
   * @return a list of keys as strings, where each key represents an object stored in the object
   *     storage
   */
  public List<String> getAllKeys() {
    return getAllKeysByPrefix("");
  }

  /**
   * Retrieves a list of all object keys in the storage that start with the specified prefix.
   *
   * @param path the prefix to match against object keys in the storage
   * @return a list of matched object keys as strings
   */
  public List<String> getAllKeysByPrefix(String path) {
    return client.listKeysByPrefix(path);
  }

  /**
   * Retrieves a list of object key information, including keys and their last modified timestamps,
   * for all objects in the storage that match the specified prefix.
   *
   * @param path the prefix to filter object keys in the storage
   * @return a list of {@code ObjectKeyInfo} instances representing the matched object keys and
   *     their last modified timestamps
   */
  public List<ObjectKeyInfo> getAllKeyInfosByPrefix(String path) {
    return client.listByPrefixWithLastModified(path);
  }

  /**
   * Retrieves the content of a file from the object storage as a string. Converts the file's binary
   * content to a UTF-8 encoded string if the file exists.
   *
   * @param filename the name of the file to retrieve from the object storage
   * @return an {@code Optional} containing the file content as a string if found, or an empty
   *     {@code Optional} if the file does not exist
   * @throws ObjectStoreServiceException if an error occurs during the retrieval process
   */
  public Optional<String> getFileAsString(String filename) throws ObjectStoreServiceException {
    Optional<byte[]> s3Response = get(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  /**
   * Retrieves an object from the object storage based on the given object key. Attempts up to a
   * fixed number of retries if an error occurs during retrieval, and handles scenarios where the
   * object key does not exist.
   *
   * @param objectKey the key identifying the object in the storage
   * @return an {@code Optional} containing the object as a byte array if found, or an empty {@code
   *     Optional} if the key does not exist
   * @throws ObjectStoreServiceException if all retries fail or an unexpected error occurs during
   *     the operation
   */
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
