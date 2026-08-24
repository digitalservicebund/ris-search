package de.bund.digitalservice.ris.search.service;

import static de.bund.digitalservice.ris.search.service.SimpleSearchQueryBuilder.convertOrderingToBoost;
import static org.opensearch.index.query.QueryBuilders.matchQuery;
import static org.opensearch.index.query.QueryBuilders.multiMatchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawDocumentTypeGroup;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Simple search type for case law. */
public class CaseLawSimpleSearchType implements SimpleSearchType {

  public static final Map<String, Float> FIELD_BOOSTS =
      Map.ofEntries(
          Map.entry(CaseLawDocumentationUnit.Fields.CASE_FACTS, convertOrderingToBoost(5)),
          Map.entry(CaseLawDocumentationUnit.Fields.COURT_KEYWORD, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.COURT_TYPE, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.DECISION_GROUNDS, convertOrderingToBoost(4)),
          Map.entry(CaseLawDocumentationUnit.Fields.DECISION_NAME, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.DEVIATING_DOCUMENT_NUMBER, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.DISSENTING_OPINION, convertOrderingToBoost(7)),
          Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENTATION_OFFICE, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENT_NUMBER, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.DOCUMENT_TYPE, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.ECLI, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.ENSUING_DECISIONS, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.FILE_NUMBERS, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.GROUNDS, convertOrderingToBoost(4)),
          Map.entry(CaseLawDocumentationUnit.Fields.GUIDING_PRINCIPLE, convertOrderingToBoost(2)),
          Map.entry(CaseLawDocumentationUnit.Fields.HAS_LEGISLATIVE_MANDATE, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.HEADLINE, convertOrderingToBoost(3)),
          Map.entry(CaseLawDocumentationUnit.Fields.HEADNOTE, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.ID, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.JUDICIAL_BODY, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.KEYWORDS, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.LEGAL_EFFECT, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.LOCATION, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.OTHER_HEADNOTE, convertOrderingToBoost(3)),
          Map.entry(CaseLawDocumentationUnit.Fields.OTHER_LONG_TEXT, convertOrderingToBoost(6)),
          Map.entry(CaseLawDocumentationUnit.Fields.OUTLINE, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.PENDING_DECISIONS, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.PREVIOUS_DECISIONS, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.PROCEDURES, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.PUBLICATION_STATUS, 1.0f),
          Map.entry(CaseLawDocumentationUnit.Fields.TENOR, convertOrderingToBoost(3)),
          Map.entry(CaseLawDocumentationUnit.Fields.TITLE_LINE, 1.0f));

  private static final List<String> EXCLUDED_FIELDS =
      List.of(
          CaseLawDocumentationUnit.Fields.CASE_FACTS,
          CaseLawDocumentationUnit.Fields.HEADNOTE,
          CaseLawDocumentationUnit.Fields.OTHER_HEADNOTE,
          CaseLawDocumentationUnit.Fields.TENOR,
          CaseLawDocumentationUnit.Fields.DISSENTING_OPINION, // note: not present in highlights
          CaseLawDocumentationUnit.Fields.GROUNDS,
          CaseLawDocumentationUnit.Fields.DECISION_GROUNDS,
          CaseLawDocumentationUnit.Fields.GUIDING_PRINCIPLE,
          CaseLawDocumentationUnit.Fields.KEYWORDS);

  public static final List<HighlightBuilder.Field> HIGHLIGHTED_FIELDS =
      List.of(
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.HEADLINE).numOfFragments(0),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.GUIDING_PRINCIPLE),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.HEADNOTE),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.OTHER_HEADNOTE),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.OUTLINE),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.TENOR),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.CASE_FACTS),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.DECISION_GROUNDS),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.GROUNDS),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.OTHER_LONG_TEXT),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.DISSENTING_OPINION),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.ECLI).noMatchSize(0),
          new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.FILE_NUMBERS).noMatchSize(0));

  private final CaseLawSearchParams searchParams;

  public CaseLawSimpleSearchType(CaseLawSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  @Override
  public Map<String, Float> getBoosts() {
    return FIELD_BOOSTS;
  }

  @Override
  public List<String> getExcludedFields() {
    return EXCLUDED_FIELDS;
  }

  @Override
  public List<HighlightBuilder.Field> getHighlightedFields() {
    return HIGHLIGHTED_FIELDS;
  }

  @Override
  public void addExtraLogic(String searchTerm, BoolQueryBuilder query) {
    if (searchParams == null) {
      return;
    }
    if (searchParams.getEcli() != null) {
      query.must(
          matchQuery(CaseLawDocumentationUnit.Fields.ECLI, searchParams.getEcli())
              .operator(Operator.AND));
    }
    if (searchParams.getFileNumber() != null) {
      query.must(
          matchQuery(CaseLawDocumentationUnit.Fields.FILE_NUMBERS, searchParams.getFileNumber())
              .operator(Operator.AND));
    }
    if (searchParams.getCourt() != null) {
      query.must(
          multiMatchQuery(
                  searchParams.getCourt(),
                  CaseLawDocumentationUnit.Fields.COURT_KEYWORD_KEYWORD,
                  CaseLawDocumentationUnit.Fields.COURT_TYPE)
              .operator(Operator.AND));
    }
    if (searchParams.getLegalEffect() != null) {
      query.must(
          matchQuery(
                  CaseLawDocumentationUnit.Fields.LEGAL_EFFECT,
                  searchParams.getLegalEffect().toString())
              .operator(Operator.AND));
    }
    if (searchParams.getType() != null) {
      queryDocumentType(searchParams.getType(), query);
    }
    if (searchParams.getTypeGroup() != null) {
      queryDocumentTypeGroup(searchParams.getTypeGroup(), query);
    }
  }

  private static void queryDocumentTypeGroup(
      CaseLawDocumentTypeGroup[] types, BoolQueryBuilder query) {
    var boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
    for (CaseLawDocumentTypeGroup group : types) {
      if (group == CaseLawDocumentTypeGroup.OTHER) {
        // query for all decisions that aren't one of the two main document types, "urteil" or
        // "beschluss"
        boolQuery.should(
            QueryBuilders.boolQuery()
                .mustNot(matchQuery(CaseLawDocumentationUnit.Fields.DOCUMENT_TYPE, "beschluss"))
                .mustNot(matchQuery(CaseLawDocumentationUnit.Fields.DOCUMENT_TYPE, "urteil")));
      } else {
        // use a match query to get subtypes, e.g., "Teilurteil" for query "Urteil"
        boolQuery.should(
            matchQuery(
                CaseLawDocumentationUnit.Fields.DOCUMENT_TYPE, group.toString().toLowerCase()));
      }
    }
    query.filter(boolQuery);
  }

  private static void queryDocumentType(@NotNull String[] types, BoolQueryBuilder query) {
    // use the document_type.keyword field to match the query exactly
    var boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
    Arrays.stream(types)
        .map(
            documentType ->
                QueryBuilders.termQuery(
                    CaseLawDocumentationUnit.Fields.DOCUMENT_TYPE + ".keyword", documentType))
        .forEach(boolQuery::should);
    query.must(boolQuery);
  }
}
