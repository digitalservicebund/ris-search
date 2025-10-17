package de.bund.digitalservice.ris.search.service;

import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public interface SimpleSearchType {

  void addHighlightedFields(HighlightBuilder builder);

  List<String> getExcludedFields();

  void addExtraLogic(String searchTerm, BoolQueryBuilder query);
}
