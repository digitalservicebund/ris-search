package de.bund.digitalservice.ris.search.utils;

import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

/**
 * Utility class for operations related to handling paginated search results from Elasticsearch.
 * This class provides methods for unwrapping search hits, converting document-based search results
 * into domain-specific entities, and manipulating string cases.
 *
 * <p>The {@code PageUtils} class is initialized with index name configurations and an Elasticsearch
 * converter to facilitate interaction with Elasticsearch search results.
 */
public class PageUtils {

  private static final Logger logger = LogManager.getLogger(PageUtils.class);

  public static <T> SearchPage<T> unwrapSearchHits(SearchHits<T> searchResult, Pageable pageable) {
    return SearchHitSupport.searchPageFor(searchResult, pageable);
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
