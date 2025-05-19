package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

/** Class to store the Lucene query tools */
public class LuceneQueryTools {

  private static final Logger logger = LogManager.getLogger(LuceneQueryTools.class);

  private static final StandardAnalyzer analyzer = new StandardAnalyzer();
  private static final QueryParser queryParser = new QueryParser("", analyzer);

  private LuceneQueryTools() {}

  private static final CustomValidationException invalidQueryException;

  static {
    CustomError error =
        CustomError.builder()
            .code("invalid_lucene_query")
            .parameter("query")
            .message("Invalid lucene query")
            .build();
    invalidQueryException = new CustomValidationException(error);
  }

  /**
   * Method to validate the parameters of a Lucene query
   *
   * @param query The query string
   * @return The decoded query string
   * @throws CustomValidationException If the query or sort parameter is invalid
   */
  public static String validateLuceneQuery(@NotNull String query) throws CustomValidationException {
    String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);

    if (!isValidLuceneQuery(decodedQuery)) {
      logger.error("Validation error(s): {}", invalidQueryException.getErrors());
      throw invalidQueryException;
    }

    return decodedQuery;
  }

  /**
   * Method to check if a Lucene query is valid
   *
   * @param queryStr The query string
   * @return True if the query is valid, false otherwise
   */
  public static boolean isValidLuceneQuery(String queryStr) {
    if (queryStr == null) return false;
    try {
      queryParser.parse(queryStr);
      return true;
    } catch (ParseException e) {
      logger.warn("Error parsing the follow query: {}", queryStr);
      return false;
    }
  }

  static final Pattern NO_MAPPING_FOUND_PATTERN =
      Pattern.compile("No mapping found for \\[([^]]+)] in order to sort on");

  public static void checkForInvalidQuery(UncategorizedElasticsearchException e)
      throws CustomValidationException {
    if (e.getCause() == null || e.getCause().getSuppressed().length == 0) {
      return;
    }
    Throwable suppressed = e.getCause().getSuppressed()[0];
    Matcher matcher = NO_MAPPING_FOUND_PATTERN.matcher(suppressed.getMessage());
    if (matcher.find()) {
      String parameter = matcher.group(1);
      String message = "Sorting is not supported for %s".formatted(parameter);
      CustomError error =
          CustomError.builder()
              .code("invalid_sort_parameter")
              .parameter("sort")
              .message(message)
              .build();
      throw new CustomValidationException(error);
    }
  }
}
