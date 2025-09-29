package de.bund.digitalservice.ris.search.service.search;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public interface SimpleSearchType {

  void addHighlightedFields(HighlightBuilder builder);

  List<String> getExcludedFields();

  void addExtraLogic(
      ParsedSearchTerm searchTerm,
      NormsSearchParams normsSearchParams,
      CaseLawSearchParams caseLawSearchParams,
      BoolQueryBuilder query);
}
