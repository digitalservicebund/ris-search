package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ChangedEcliCrawlerDocumentsIterator implements Iterator<List<EcliCrawlerDocument>> {

  List<EcliCrawlerDocument> ecliDocuments;
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
    ecliDocuments = new ArrayList<>();
    this.resultSize = resultSize;
    getNext();
  }

  /** populate desired changes for changed and deleted until the resultSize is reached */
  private void getNext() {
    ecliDocuments.clear();

    if (!changed.isEmpty()) {
      changed = fillAccumulator(changed, id -> changedSupplier.get(id), ecliDocuments);
    }
    if (!deleted.isEmpty()) {
      deleted = fillAccumulator(deleted, id -> deleteSupplier.get(id), ecliDocuments);
    }
  }

  /**
   * fills the accumulator up to the specified maximum size
   *
   * @param ids list of ids that return an Optional of EcliCrawlerDocument
   * @param supplier Function that returns an Optional of EcliCrawlerDocument based on its
   *     identifier
   * @param acc Accumulated List of returned EcliCrawlerDocuments
   * @return remaining ids
   */
  private List<String> fillAccumulator(
      List<String> ids, Supplier supplier, List<EcliCrawlerDocument> acc) {
    int numTaken = 0;
    for (int i = 0; i < ids.size() && acc.size() < resultSize; i++) {
      supplier.get(ids.get(i)).ifPresent(acc::add);
      numTaken++;
    }
    return ids.subList(numTaken, ids.size());
  }

  @Override
  public boolean hasNext() {
    return !ecliDocuments.isEmpty();
  }

  @Override
  public List<EcliCrawlerDocument> next() {
    if (ecliDocuments.isEmpty()) {
      throw new NoSuchElementException();
    }
    List<EcliCrawlerDocument> next = new ArrayList<>(ecliDocuments);
    // populate upcoming iterator result in advance
    getNext();
    return next;
  }
}
