package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ObjectStorageClient {
  public List<String> listKeysByPrefix(String path);

  List<ObjectKeyInfo> listByPrefixWithLastModified(String prefix);

  public FilterInputStream getStream(String objectKey) throws NoSuchKeyException;

  public void save(String fileName, String fileContent);

  public void close();

  public void delete(String fileName);

  public long putStream(String resultObjectKey, InputStream pipedInputStream) throws IOException;
}
