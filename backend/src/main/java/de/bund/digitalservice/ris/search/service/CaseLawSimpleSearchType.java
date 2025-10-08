package de.bund.digitalservice.ris.search.service;

import static org.opensearch.index.query.QueryBuilders.matchQuery;
import static org.opensearch.index.query.QueryBuilders.multiMatchQuery;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawDocumentTypeGroup;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

public class CaseLawSimpleSearchType implements SimpleSearchType {

  private static final List<String> CASE_LAW_HIGHLIGHT_CONTENT_FIELDS =
      List.of(
          CaseLawDocumentationUnit.Fields.HEADLINE,
          CaseLawDocumentationUnit.Fields.GUIDING_PRINCIPLE,
          CaseLawDocumentationUnit.Fields.HEADNOTE,
          CaseLawDocumentationUnit.Fields.OTHER_HEADNOTE,
          CaseLawDocumentationUnit.Fields.OUTLINE,
          CaseLawDocumentationUnit.Fields.TENOR,
          CaseLawDocumentationUnit.Fields.CASE_FACTS,
          CaseLawDocumentationUnit.Fields.DECISION_GROUNDS,
          CaseLawDocumentationUnit.Fields.GROUNDS,
          CaseLawDocumentationUnit.Fields.OTHER_LONG_TEXT,
          CaseLawDocumentationUnit.Fields.DISSENTING_OPINION);

  private static final List<String> CASE_LAW_FETCH_EXCLUDED_FIELDS =
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

  private final CaseLawSearchParams searchParams;

  public CaseLawSimpleSearchType(CaseLawSearchParams searchParams) {
    this.searchParams = searchParams;
  }

  @Override
  public void addHighlightedFields(HighlightBuilder builder) {
    addHighlightedFieldsStatic(builder);
  }

  public static void addHighlightedFieldsStatic(HighlightBuilder builder) {
    CASE_LAW_HIGHLIGHT_CONTENT_FIELDS.forEach(builder::field);
    // ECLI and FILE_NUMBERS are returned in _source and therefore not needed when not matched
    builder.field(new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.ECLI).noMatchSize(0));
    builder.field(
        new HighlightBuilder.Field(CaseLawDocumentationUnit.Fields.FILE_NUMBERS).noMatchSize(0));
  }

  @Override
  public List<String> getExcludedFields() {
    return CASE_LAW_FETCH_EXCLUDED_FIELDS;
  }

  @Override
  public void addExtraLogic(ParsedSearchTerm searchTerm, BoolQueryBuilder query) {
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
