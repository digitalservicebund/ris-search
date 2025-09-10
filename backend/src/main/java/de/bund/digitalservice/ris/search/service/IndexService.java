package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;

public interface IndexService {

  void reindexAll(String startingTimestamp) throws ObjectStoreServiceException;

  void indexChangelog(Changelog changelog) throws ObjectStoreServiceException;

  int getNumberOfIndexedDocuments();

  int getNumberOfIndexableDocumentsInBucket();
}
