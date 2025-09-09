package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.Bucket;
import de.bund.digitalservice.ris.search.repository.opensearch.OpensearchRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.Logger;

public interface IndexService {

  void reindexAll(String startingTimestamp) throws ObjectStoreServiceException;

  void indexChangelog(Changelog changelog) throws ObjectStoreServiceException;

  int getNumberOfIndexedEntities();

  int getNumberOfIndexableDocumentsInBucket();
}
