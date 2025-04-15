package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.RetryableObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;

public interface IndexService {
  void reindexAll(String startingTimestamp) throws RetryableObjectStoreException;

  void indexChangelog(String key, Changelog changelog) throws RetryableObjectStoreException;

  int getNumberOfIndexedDocuments();

  int getNumberOfFilesInBucket();
}
