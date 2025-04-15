package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

@Component
public class PageUtils {

  private final String caseLawsIndexName;
  private final String normsIndexName;

  @Autowired
  public PageUtils(Configurations configurations) {
    caseLawsIndexName = configurations.getCaseLawsIndexName();
    normsIndexName = configurations.getNormsIndexName();
  }

  @SuppressWarnings("unchecked")
  public static <T> SearchPage<T> unwrapSearchHits(SearchHits<T> searchResult, Pageable pageable) {
    return SearchHitSupport.searchPageFor(searchResult, pageable);
  }

  public SearchPage<AbstractSearchEntity> unwrapMixedSearchHits(
      SearchHits<Document> searchHits, Pageable pageable, ElasticsearchConverter converter) {

    List<SearchHit<AbstractSearchEntity>> convertedSearchHits =
        searchHits.stream().map(searchHit -> convertSearchHit(searchHit, converter)).toList();

    SearchHits<AbstractSearchEntity> finalSearchHits =
        new SearchHitsImpl<>(
            searchHits.getTotalHits(),
            searchHits.getTotalHitsRelation(),
            searchHits.getMaxScore(),
            "",
            searchHits.getPointInTimeId(),
            convertedSearchHits,
            searchHits.getAggregations(),
            searchHits.getSuggest(),
            searchHits.getSearchShardStatistics());

    return SearchHitSupport.searchPageFor(finalSearchHits, pageable);
  }

  public SearchHit<AbstractSearchEntity> convertSearchHit(
      SearchHit<Document> searchHit, ElasticsearchConverter converter) {
    AbstractSearchEntity entity;
    String indexName = searchHit.getIndex();

    if (indexName != null && indexName.startsWith(caseLawsIndexName)) {
      entity = converter.read(CaseLawDocumentationUnit.class, searchHit.getContent());
    } else if (indexName != null && indexName.startsWith(normsIndexName)) {
      entity = converter.read(Norm.class, searchHit.getContent());
    } else {
      throw new IllegalStateException("Unexpected value: " + searchHit.getIndex());
    }

    return new SearchHit<>(
        searchHit.getIndex(),
        searchHit.getId(),
        searchHit.getRouting(),
        searchHit.getScore(),
        searchHit.getSortValues().toArray(),
        // For mixed search hits, the keys of the highlightFields Map must be converted manually.
        // If a single index is queried, the names are converted automatically by {@link
        // ElasticsearchOperations}.
        convertSnakeCaseKeysToCamelCase(searchHit.getHighlightFields()),
        searchHit.getInnerHits(),
        searchHit.getNestedMetaData(),
        searchHit.getExplanation(),
        searchHit.getMatchedQueries(),
        entity);
  }

  public static final Pattern SNAKE_CASE_PATTERN = Pattern.compile("_([a-z])");

  private static String snakeCaseToCamelCase(String str) {
    return SNAKE_CASE_PATTERN.matcher(str).replaceAll(m -> m.group(1).toUpperCase());
  }

  public static <T> Map<String, T> convertSnakeCaseKeysToCamelCase(Map<String, T> map) {
    // Convert the key from snake_case to camelCase
    // Add a new entry with the converted key and original value to the newMap
    return map.entrySet().stream()
        .collect(Collectors.toMap(e -> snakeCaseToCamelCase(e.getKey()), Map.Entry::getValue));
  }
}
