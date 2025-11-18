package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * This class is a dummy implementation of an {@link ObjectStorageClient} class. It is used for
 * environments where no bucket exists yet and simulates an empty bucket.
 */
@Component
public class ObjectStorageClientDummy implements ObjectStorageClient {
  @Override
  public List<String> listKeysByPrefix(String path) {
    return List.of();
  }

  @Override
  public List<ObjectKeyInfo> listByPrefixWithLastModified(String prefix) {
    return List.of();
  }

  @Override
  public FilterInputStream getStream(String objectKey) throws NoSuchKeyException {
    throw new NoSuchKeyException("Key not found", new ObjectStoreServiceException("Key not found"));
  }

  @Override
  public void save(String fileName, String fileContent) {
    /* no need to implement persist operations on dummy implementation */
  }

  @Override
  public void close() {
    /* no resource is actually opened */
  }

  @Override
  public void delete(String fileName) {
    /* no need to implement persist operations on dummy implementation */
  }

  @Override
  public long putStream(String resultObjectKey, InputStream pipedInputStream) throws IOException {
    return 0;
  }
}
