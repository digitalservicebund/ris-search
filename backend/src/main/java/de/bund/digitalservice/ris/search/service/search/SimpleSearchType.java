package de.bund.digitalservice.ris.search.service.search;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public interface SimpleSearchType {

  void addHighlightedFields(HighlightBuilder builder);

  List<String> getExcludedFields();

  void addExtraLogic(ParsedSearchTerm searchTerm, BoolQueryBuilder query);
}
