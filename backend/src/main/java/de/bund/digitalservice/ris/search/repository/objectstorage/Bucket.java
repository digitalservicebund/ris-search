package de.bund.digitalservice.ris.search.repository.objectstorage;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import java.util.List;
import java.util.Optional;

public interface Bucket {
  Optional<String> getFileAsString(String filename) throws ObjectStoreServiceException;

  List<String> getAllKeys();
}
