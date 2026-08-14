package de.bund.digitalservice.ris.search.service.eclicrawler;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.jspecify.annotations.NonNull;

/**
 * Iterator implementation for retrieving lists of EcliCrawlerDocument objects that represent
 * changed or deleted case law documents, based on provided identifiers and suppliers. The Iterator
 * will return the Documents in chunks of 10000 except the last one, which might be less.
 */
public class ChangedEcliCrawlerDocumentsIterator implements Iterator<List<EcliCrawlerDocument>> {

  private static final int ID_CHUNK_SIZE = 10_000;

  private final Queue<EcliCrawlerDocument> ecliDocumentsBuffer = new ArrayDeque<>();
  private final Supplier changedSupplier;
  private final Supplier deleteSupplier;
  private List<String> changed;
  private List<String> deleted;
  private final int resultSize;

  /** Functional interface for supplying EcliCrawlerDocuments based on their identifier */
  @FunctionalInterface
  public interface Supplier {
    @NonNull List<EcliCrawlerDocument> get(List<String> ids);
  }

  /**
   * Constructor
   *
   * @param changedSupplier Supplier for changed EcliCrawlerDocuments
   * @param deleteSupplier Supplier for deleted EcliCrawlerDocuments
   * @param changed List of changed document IDs
   * @param deleted List of deleted document IDs
   * @param resultSize Target batch size for iterator output
   */
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
    this.resultSize = resultSize;
    getNext();
  }

  /** Populate buffer with documents until resultSize is reached or IDs are exhausted */
  private void getNext() {
    while (ecliDocumentsBuffer.size() < resultSize && !changed.isEmpty()) {
      changed = fillBuffer(changed, changedSupplier);
    }
    while (ecliDocumentsBuffer.size() < resultSize && !deleted.isEmpty()) {
      deleted = fillBuffer(deleted, deleteSupplier);
    }
  }

  /**
   * Greedily fetches chunks from supplier until the buffer reaches resultSize or the ID list is
   * exhausted.
   *
   * @param ids remaining ID list
   * @param supplier Function returning documents for a list of IDs
   * @return remaining IDs not yet queried
   */
  private List<String> fillBuffer(List<String> ids, Supplier supplier) {
    int numTaken = 0;

    while (numTaken < ids.size() && ecliDocumentsBuffer.size() < resultSize) {
      // Safely calculate chunk size so we never exceed ids.size()
      int currentBatchSize = Math.min(ID_CHUNK_SIZE, ids.size() - numTaken);
      List<String> batch = ids.subList(numTaken, numTaken + currentBatchSize);

      List<EcliCrawlerDocument> documents = supplier.get(batch);
      ecliDocumentsBuffer.addAll(documents);

      numTaken += currentBatchSize;
    }

    return ids.subList(numTaken, ids.size());
  }

  @Override
  public boolean hasNext() {
    return !ecliDocumentsBuffer.isEmpty();
  }

  @Override
  public List<EcliCrawlerDocument> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    List<EcliCrawlerDocument> result = new ArrayList<>();
    while (!ecliDocumentsBuffer.isEmpty() && result.size() < resultSize) {
      result.add(ecliDocumentsBuffer.poll());
    }

    // Top up buffer for subsequent hasNext() / next() calls
    getNext();

    return result;
  }
}
