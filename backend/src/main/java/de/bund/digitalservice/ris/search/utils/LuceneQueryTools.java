package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.exception.CustomValidationException;
import de.bund.digitalservice.ris.search.models.errors.CustomError;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryVisitor;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;

/** Class to store the Lucene query tools */
public class LuceneQueryTools {

  private static final StandardAnalyzer analyzer = new StandardAnalyzer();

  private LuceneQueryTools() {}

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
      throw buildLuceneErrorMessage(e);
    }
  }

  private static CustomValidationException buildLuceneErrorMessage(ParseException e) {
    return new CustomValidationException(
        CustomError.builder()
            .code("invalid_lucene_query")
            .parameter("query")
            .message(e.getMessage())
            .build());
  }

  /**
   * Transforms a query string by extracting all terms and combining them with OR. Special
   * characters are escaped.
   *
   * @param queryString to be transformed
   * @return A new query string with all terms OR-combined
   * @throws CustomValidationException in case a queryString is invalid
   */
  public static String joinAllTermsWithOr(String queryString) throws CustomValidationException {
    if (queryString == null || queryString.isBlank()) {
      return "";
    }

    try {
      // using a KeywordAnalyzer to avoid dates being split up into separate terms
      QueryParser queryParser = new QueryParser("", new KeywordAnalyzer());
      var terms = collectTerms(queryParser.parse(queryString));
      return terms.stream()
          .map(t -> t.field().isBlank() ? t.text() : t.field() + ":" + t.text())
          .collect(Collectors.joining(" OR "));
    } catch (ParseException e) {
      throw buildLuceneErrorMessage(e);
    }
  }

  /**
   * Collects all terms of a given Query. Escapes all terms during collection.
   *
   * @param query to collect all terms from
   * @return Set of terms with escaped text fields
   */
  private static Set<Term> collectTerms(Query query) {
    Set<Term> terms = new LinkedHashSet<>();

    query.visit(
        new QueryVisitor() {

          @Override
          public void consumeTerms(Query query, Term... visitedTerms) {
            terms.addAll(
                Arrays.stream(visitedTerms)
                    .map(t -> new Term(t.field(), QueryParserBase.escape(t.text())))
                    .toList());
          }

          @Override
          public QueryVisitor getSubVisitor(BooleanClause.Occur occur, Query parent) {
            return this;
          }
        });

    return terms;
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
