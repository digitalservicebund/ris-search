package de.bund.digitalservice.ris.search.service;

import java.util.List;
import java.util.Map;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Interface defining methods for different simple search types. */
public interface SimpleSearchType {
  Map<String, Float> getBoosts();

  List<String> getExcludedFields();

  List<HighlightBuilder.Field> getHighlightedFields();

  void addExtraLogic(String searchTerm, BoolQueryBuilder query);
}
