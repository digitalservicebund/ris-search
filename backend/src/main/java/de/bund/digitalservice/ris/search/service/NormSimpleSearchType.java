package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;
import static org.opensearch.index.query.QueryBuilders.termQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.NormsSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.utils.DateUtils;
import java.util.List;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Service class for interacting with the database and return the search results. */
public class NormSimpleSearchType implements SimpleSearchType {

  public static final List<String> NORMS_FETCH_EXCLUDED_FIELDS =
      List.of(
          Norm.Fields.ARTICLE_NAMES,
          Norm.Fields.ARTICLE_TEXTS,
          Norm.Fields.ARTICLES,
          Norm.Fields.TABLE_OF_CONTENTS);

  private final NormsSearchParams normsSearchParams;

  public NormSimpleSearchType(NormsSearchParams normsSearchParams) {
    this.normsSearchParams = normsSearchParams;
  }

  @Override
  public List<String> getExcludedFields() {
    return NORMS_FETCH_EXCLUDED_FIELDS;
  }

  @Override
  public List<HighlightBuilder.Field> getHighlightedFields() {
    return getHighlightedFieldsStatic();
  }

  public static List<HighlightBuilder.Field> getHighlightedFieldsStatic() {
    return List.of(new HighlightBuilder.Field(Norm.Fields.OFFICIAL_TITLE).numOfFragments(0));
  }

  @Override
  public void addExtraLogic(String searchTerm, BoolQueryBuilder query) {

    if (normsSearchParams == null) {
      return;
    }

    if (normsSearchParams.getEli() != null) {
      query.must(
          matchQuery(Norm.Fields.WORK_ELI, normsSearchParams.getEli()).operator(Operator.AND));
    }

    if (normsSearchParams.getAbbreviation() != null) {
      query.must(
          termQuery(
              Norm.Fields.OFFICIAL_ABBREVIATION_KEYWORD, normsSearchParams.getAbbreviation()));
    }

    if (normsSearchParams.getMostRelevantOn() != null) {
      BoolQueryBuilder isNotNorm =
          QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Norm.Fields.EXPRESSION_ELI));

      BoolQueryBuilder isMostRelevant = QueryBuilders.boolQuery();
      isMostRelevant.filter(
          QueryBuilders.rangeQuery(Norm.Fields.TIME_RELEVANCE_START_DATE)
              .lte(normsSearchParams.getMostRelevantOn()));
      isMostRelevant.filter(
          QueryBuilders.rangeQuery(Norm.Fields.TIME_RELEVANCE_END_DATE)
              .gte(normsSearchParams.getMostRelevantOn()));

      BoolQueryBuilder either = QueryBuilders.boolQuery().minimumShouldMatch(1);
      either.should(isMostRelevant);
      either.should(isNotNorm);
      query.must(either);
    }
    DateUtils.buildQueryForTemporalCoverage(
            normsSearchParams.getTemporalCoverageFrom(), normsSearchParams.getTemporalCoverageTo())
        .ifPresent(query::filter);
  }
}
