package de.bund.digitalservice.ris.search.utils;

import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.collections4.ListUtils;

public class BatchUtils {

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
