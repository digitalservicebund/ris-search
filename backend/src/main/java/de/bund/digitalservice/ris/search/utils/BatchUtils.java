package de.bund.digitalservice.ris.search.utils;

import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.collections4.ListUtils;

/** Utility class for batch related operations. */
public class BatchUtils {

  private BatchUtils() {}

  /**
   * Processes a function over a list splitting the calls into batches.
   *
   * @param list The list to be processed.
   * @param batchSize The size of the batching
   * @param batchProcessor The method to be applied while processing.
   */
  public static <T> void processInBatches(
      List<T> list, int batchSize, Consumer<List<T>> batchProcessor) {
    if (list == null || list.isEmpty() || batchSize <= 0) {
      return;
    }
    List<List<T>> partitions = ListUtils.partition(list, batchSize);
    for (List<T> partition : partitions) {
      batchProcessor.accept(partition);
    }
  }
}
