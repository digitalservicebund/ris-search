package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class ChangedEcliCrawlerDocumentsIterator implements Iterator<List<EcliCrawlerDocument>> {

  ArrayBlockingQueue<EcliCrawlerDocument> ecliDocumentsBuffer;
  Supplier changedSupplier;
  Supplier deleteSupplier;
  List<String> changed;
  List<String> deleted;
  final int resultSize;

  @FunctionalInterface
  public interface Supplier {
    Optional<EcliCrawlerDocument> get(String id);
  }

  public ChangedEcliCrawlerDocumentsIterator(
      Supplier changedSupplier,
      Supplier deleteSupplier,
      List<String> changed,
      List<String> deleted,
      int resultSize) {
    this.changedSupplier = changedSupplier;
    this.deleteSupplier = deleteSupplier;
    this.changed = new ArrayList<>(changed);
    this.deleted = new ArrayList<>(deleted);
    ecliDocumentsBuffer = new ArrayBlockingQueue<>(resultSize);
    this.resultSize = resultSize;
    getNext();
  }

  /** populate desired changes for changed and deleted until the resultSize is reached */
  private void getNext() {
    ecliDocumentsBuffer.clear();

    if (!changed.isEmpty()) {
      changed = fillBuffer(changed, id -> changedSupplier.get(id));
    }
    if (!deleted.isEmpty()) {
      deleted = fillBuffer(deleted, id -> deleteSupplier.get(id));
    }
  }

  /**
   * fills the accumulator up to the specified maximum size
   *
   * @param ids list of ids that return an Optional of EcliCrawlerDocument
   * @param supplier Function that returns an Optional of EcliCrawlerDocument based on its
   *     identifier
   * @return remaining ids
   */
  private List<String> fillBuffer(List<String> ids, Supplier supplier) {
    int numTaken = 0;
    for (int i = 0; i < ids.size() && ecliDocumentsBuffer.remainingCapacity() > 0; i++) {
      supplier.get(ids.get(i)).ifPresent(ecliDocumentsBuffer::add);
      numTaken++;
    }
    return ids.subList(numTaken, ids.size());
  }

  @Override
  public boolean hasNext() {
    return !ecliDocumentsBuffer.isEmpty();
  }

  @Override
  public List<EcliCrawlerDocument> next() {
    if (ecliDocumentsBuffer.isEmpty()) {
      throw new NoSuchElementException();
    }
    List<EcliCrawlerDocument> next = new ArrayList<>(ecliDocumentsBuffer);
    // populate upcoming iterator result in advance
    getNext();
    return next;
  }
}
