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
public class LiteratureBucketDummy extends LiteratureBucket {

  public LiteratureBucketDummy() {
    super(null, null);
  }

  @Override
  public List<String> getAllKeys() {
    return Collections.emptyList();
  }

  @Override
  public List<String> getAllKeysByPrefix(String path) {
    return Collections.emptyList();
  }

  @Override
  public Optional<String> getFileAsString(String filename) throws ObjectStoreServiceException {
    Optional<byte[]> s3Response = get(filename);
    return s3Response.map(bytes -> new String(bytes, StandardCharsets.UTF_8));
  }

  @Override
  public Optional<byte[]> get(String objectKey) throws ObjectStoreServiceException {
    return Optional.empty();
  }

  @Override
  public FilterInputStream getStream(String objectKey) throws NoSuchKeyException {
    throw new NoSuchKeyException("Key not found", new ObjectStoreServiceException("Key not found"));
  }

  @Override
  public List<ObjectKeyInfo> getAllKeyInfosByPrefix(String path) {
    return Collections.emptyList();
  }

  @Override
  public void delete(String fileName) {
    /**/
  }

  @Override
  public void save(String fileName, String fileContent) {
    /**/
  }

  @Override
  public void close() {
    /**/
  }

  @Override
  public long putStream(String objectKey, InputStream inputStream) throws IOException {
    return 0;
  }
}
