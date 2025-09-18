package de.bund.digitalservice.ris.search.repository.objectstorage.literature;

import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorage;
import de.bund.digitalservice.ris.search.repository.objectstorage.ObjectStorageClient;
import org.apache.logging.log4j.Logger;

public abstract class LiteratureBucket extends ObjectStorage {
  LiteratureBucket(ObjectStorageClient client, Logger logger) {
    super(client, logger);
  }
}
