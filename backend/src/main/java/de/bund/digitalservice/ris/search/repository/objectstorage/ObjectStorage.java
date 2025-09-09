package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.NoSuchKeyException;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface ObjectStorage {

  List<String> getAllKeys();

  List<String> getAllKeysByPrefix(String path);

  Optional<String> getFileAsString(String filename) throws ObjectStoreServiceException;

  Optional<byte[]> get(String objectKey) throws ObjectStoreServiceException;

  List<ObjectKeyInfo> getAllKeyInfosByPrefix(String path);

  FilterInputStream getStream(String objectKey) throws NoSuchKeyException;

  void delete(String fileName);

  void save(String fileName, String fileContent);

  void close();

  long putStream(String objectKey, InputStream inputStream) throws IOException;
}
