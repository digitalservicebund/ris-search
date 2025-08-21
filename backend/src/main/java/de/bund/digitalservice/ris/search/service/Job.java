package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import org.springframework.scheduling.annotation.Async;

public interface Job {
  void runJob() throws ObjectStoreServiceException;

  @Async
  void runJobAsync() throws ObjectStoreServiceException;
}
