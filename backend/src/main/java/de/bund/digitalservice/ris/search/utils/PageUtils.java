package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.config.opensearch.Configurations;
import de.bund.digitalservice.ris.search.models.opensearch.AbstractSearchEntity;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Utility class for operations related to handling paginated search results from Elasticsearch.
 * This class provides methods for unwrapping search hits, converting document-based search results
 * into domain-specific entities, and manipulating string cases.
 *
 * <p>The {@code PageUtils} class is initialized with index name configurations and an Elasticsearch
 * converter to facilitate interaction with Elasticsearch search results.
 */
@Component
public class PageUtils {

  private final String caseLawsIndexName;
  private final String literatureIndexName;
  private final String normsIndexName;
  private final String administrativeDirectiveIndexName;
  private final ElasticsearchConverter elasticsearchConverter;

  private static final Logger logger = LogManager.getLogger(PageUtils.class);

  /**
   * Constructs a {@code PageUtils} instance with the necessary index names and converter. The
   * values for index names are fetched from the provided {@link Configurations} object.
   *
   * @param configurations the configuration object that provides index names used in Elasticsearch
   * @param elasticsearchConverter the converter for handling Elasticsearch-related operations
   */
  @Autowired
  public PageUtils(Configurations configurations, ElasticsearchConverter elasticsearchConverter) {
    this.caseLawsIndexName = configurations.getCaseLawsIndexName();
    this.literatureIndexName = configurations.getLiteratureIndexName();
    this.normsIndexName = configurations.getNormsIndexName();
    this.administrativeDirectiveIndexName = configurations.getAdministrativeDirectiveIndexName();
    this.elasticsearchConverter = elasticsearchConverter;
  }

  public static <T> SearchPage<T> unwrapSearchHits(SearchHits<T> searchResult, Pageable pageable) {
    return SearchHitSupport.searchPageFor(searchResult, pageable);
  }

  /**
   * Unwraps a mixed set of {@link SearchHits} containing {@link Document} elements into a {@link
   * SearchPage} of {@link AbstractSearchEntity} elements.
   *
   * <p>This method processes a collection of search hits, converting each {@link SearchHit}
   * containing a {@link Document} into a corresponding {@link AbstractSearchEntity}, based on the
   * index name. Successfully converted hits are assembled into a {@link SearchPage} for pagination
   * purposes.
   *
   * @param searchHits the set of search hits containing {@link Document} instances to be processed
   *     and converted
   * @param pageable the {@link Pageable} configuration for pagination, including page size and
   *     sorting order
   * @return a {@link SearchPage} of converted {@link AbstractSearchEntity} instances, ready for use
   *     in paginated results
   */
  public SearchPage<AbstractSearchEntity> unwrapMixedSearchHits(
      SearchHits<Document> searchHits, Pageable pageable) {

    List<SearchHit<AbstractSearchEntity>> convertedSearchHits =
        searchHits.stream()
            .map(this::convertSearchHit)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    SearchHits<AbstractSearchEntity> finalSearchHits =
        new SearchHitsImpl<>(
            searchHits.getTotalHits(),
            searchHits.getTotalHitsRelation(),
            searchHits.getMaxScore(),
            searchHits.getExecutionDuration(),
            "",
            searchHits.getPointInTimeId(),
            convertedSearchHits,
            searchHits.getAggregations(),
            searchHits.getSuggest(),
            searchHits.getSearchShardStatistics());

    return SearchHitSupport.searchPageFor(finalSearchHits, pageable);
  }

  /**
   * Converts a {@link SearchHit} of type {@link Document} into an {@link Optional} containing a
   * {@link SearchHit} of type {@link AbstractSearchEntity}, based on the index name.
   *
   * <p>The method inspects the index name of the input {@code searchHit} and attempts to map the
   * document content to a specific implementation of {@link AbstractSearchEntity}. If the index
   * name matches a known pattern, the document is converted accordingly. For unsupported or
   * unexpected index names, the method returns an empty {@link Optional}.
   *
   * @param searchHit the search hit containing the document to be converted, including metadata
   *     such as the index name, id, and score
   * @return an {@link Optional} containing a {@link SearchHit} of type {@link AbstractSearchEntity}
   *     if the conversion is successful; otherwise, an empty {@link Optional}
   */
  public Optional<SearchHit<AbstractSearchEntity>> convertSearchHit(SearchHit<Document> searchHit) {
    AbstractSearchEntity entity;
    String indexName = searchHit.getIndex();

    if (indexName != null && indexName.startsWith(caseLawsIndexName)) {
      entity = elasticsearchConverter.read(CaseLawDocumentationUnit.class, searchHit.getContent());
    } else if (indexName != null && indexName.startsWith(literatureIndexName)) {
      entity = elasticsearchConverter.read(Literature.class, searchHit.getContent());
    } else if (indexName != null && indexName.startsWith(normsIndexName)) {
      entity = elasticsearchConverter.read(Norm.class, searchHit.getContent());
    } else if (indexName != null && indexName.startsWith(administrativeDirectiveIndexName)) {
      return Optional.empty();
    } else {
      logger.error("Unexpected index on document search {}", searchHit.getIndex());
      return Optional.empty();
    }

    return Optional.of(
        new SearchHit<>(
            searchHit.getIndex(),
            searchHit.getId(),
            searchHit.getRouting(),
            searchHit.getScore(),
            searchHit.getSortValues().toArray(),
            // For mixed search hits, the keys of the highlightFields Map must be converted
            // manually.
            // If a single index is queried, the names are converted automatically by {@link
            // ElasticsearchOperations}.
            searchHit.getHighlightFields(),
            searchHit.getInnerHits(),
            searchHit.getNestedMetaData(),
            searchHit.getExplanation(),
            searchHit.getMatchedQueries(),
            entity));
  }

  public static final Pattern SNAKE_CASE_PATTERN = Pattern.compile("_([a-z])");

  /**
   * Converts a given string from snake_case to camelCase format. If the input string does not
   * contain underscores, the original string is returned unchanged.
   *
   * @param str the input string formatted in snake_case
   * @return the string converted to camelCase
   */
  public static String snakeCaseToCamelCase(String str) {
    if (!str.contains("_")) return str;
    return SNAKE_CASE_PATTERN.matcher(str).replaceAll(m -> m.group(1).toUpperCase());
  }
}
