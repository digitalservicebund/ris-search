package de.bund.digitalservice.ris.search.repository.objectstorage.literature;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectKeyInfo;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * This class is a dummy implementation of the actual {@link LiteratureBucket} class. It is used for
 * environments where no bucket exists yet and simulates an empty bucket.
 */
@Component
@Profile({"uat", "production", "prototype"})
public class LiteratureBucketDummy implements LiteratureBucket {

  public LiteratureBucketDummy() {
    /**/
  }

  public List<String> getAllKeys() {
    return Collections.emptyList();
  }

  public List<String> getAllKeysByPrefix(String path) {
    return Collections.emptyList();
  }

  public Optional<String> getFileAsString(String filename) throws ObjectStoreServiceException {
    Optional<byte[]> s3Response = get(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  public Optional<byte[]> get(String objectKey) throws ObjectStoreServiceException {
    return Optional.empty();
  }

  public FilterInputStream getStream(String objectKey) throws NoSuchKeyException {
    throw new NoSuchKeyException("Key not found", new ObjectStoreServiceException("Key not found"));
  }

  public List<ObjectKeyInfo> getAllKeyInfosByPrefix(String path) {
    return Collections.emptyList();
  }

  public void delete(String fileName) {
    /**/
  }

  public void save(String fileName, String fileContent) {
    /**/
  }

  public void close() {
    /**/
  }

  public long putStream(String objectKey, InputStream inputStream) throws IOException {
    return 0;
  }
}
