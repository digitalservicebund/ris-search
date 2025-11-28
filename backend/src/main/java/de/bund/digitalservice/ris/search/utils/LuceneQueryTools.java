package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

/** Class to store the Lucene query tools */
public class LuceneQueryTools {

  private static final Logger logger = LogManager.getLogger(LuceneQueryTools.class);

  private static final StandardAnalyzer analyzer = new StandardAnalyzer();

  private LuceneQueryTools() {}

  private static final CustomValidationException invalidQueryException =
      new CustomValidationException(
          CustomError.builder()
              .code("invalid_lucene_query")
              .parameter("query")
              .message("Invalid lucene query")
              .build());

  /**
   * Method to validate the parameters of a Lucene query
   *
   * @param query The query string
   * @throws CustomValidationException If the query or sort parameter is invalid
   */
  public static void validateLuceneQuery(String query) throws CustomValidationException {
    if (StringUtils.isEmpty(query)) {
      return;
    }
    try {
      QueryParser queryParser = new QueryParser("", analyzer);
      queryParser.parse(query);
    } catch (ParseException e) {
      logger.error("Validation error(s): {}", invalidQueryException.getErrors());
      throw invalidQueryException;
    }
  }

  static final Pattern NO_MAPPING_FOUND_PATTERN =
      Pattern.compile("No mapping found for \\[([^]]+)] in order to sort on");

  /**
   * Checks for invalid sort queries in an Elasticsearch exception and throws a custom validation
   * exception if necessary.
   *
   * <p>The method identifies if the exception contains a suppressed cause with an error message
   * indicating that a sort parameter is unsupported due to missing mapping. If such a condition is
   * met, a {@code CustomValidationException} is constructed and thrown with detailed error
   * information.
   *
   * @param e An instance of {@code UncategorizedElasticsearchException} containing details of the
   *     error encountered in Elasticsearch operation.
   * @throws CustomValidationException If the exception indicates that sorting is not supported for
   *     a given parameter due to missing mappings.
   */
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
