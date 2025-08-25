package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;

public interface Job {
  void runJob() throws ObjectStoreServiceException;
}
