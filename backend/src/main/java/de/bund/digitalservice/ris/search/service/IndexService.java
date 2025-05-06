package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;

public interface IndexService {
  void reindexAll(String startingTimestamp) throws ObjectStoreException;

  void indexChangelog(String key, Changelog changelog) throws ObjectStoreException;

  int getNumberOfIndexedDocuments();

  int getNumberOfFilesInBucket();
}
