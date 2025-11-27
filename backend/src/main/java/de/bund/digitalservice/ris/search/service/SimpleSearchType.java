package de.bund.digitalservice.ris.search.service;

import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public interface SimpleSearchType {

  List<String> getExcludedFields();

  List<HighlightBuilder.Field> getHighlightedFields();

  void addExtraLogic(String searchTerm, BoolQueryBuilder query);
}
