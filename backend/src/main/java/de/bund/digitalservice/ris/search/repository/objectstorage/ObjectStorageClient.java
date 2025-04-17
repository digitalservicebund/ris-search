package de.bund.digitalservice.ris.search.repository.objectstorage;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.util.List;

public interface ObjectStorageClient {
  public List<String> getAllFilenamesByPath(String path);

  public FilterInputStream getStream(String objectKey) throws FileNotFoundException;

  public void save(String fileName, String fileContent);

  public void close();

  public void delete(String fileName);
}
