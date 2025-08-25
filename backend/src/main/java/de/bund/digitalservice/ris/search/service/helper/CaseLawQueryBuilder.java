package de.bund.digitalservice.ris.search.service.helper;

import static org.opensearch.index.query.QueryBuilders.matchQuery;
import static org.opensearch.index.query.QueryBuilders.multiMatchQuery;

import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawDocumentTypeGroup;
import de.bund.digitalservice.ris.search.models.api.parameters.CaseLawSearchParams;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;

public class CaseLawQueryBuilder {

  private CaseLawQueryBuilder() {}

  public static void addCaseLawFilters(CaseLawSearchParams params, BoolQueryBuilder query) {
    if (params == null) {
      return;
    }
    if (params.getEcli() != null) {
      query.must(
          matchQuery(CaseLawDocumentationUnit.Fields.ECLI, params.getEcli())
              .operator(Operator.AND));
    }
    if (params.getFileNumber() != null) {
      query.must(
          matchQuery(CaseLawDocumentationUnit.Fields.FILE_NUMBERS, params.getFileNumber())
              .operator(Operator.AND));
    }
    if (params.getCourt() != null) {
      query.must(
          multiMatchQuery(
                  params.getCourt(),
                  CaseLawDocumentationUnit.Fields.COURT_KEYWORD_KEYWORD,
                  CaseLawDocumentationUnit.Fields.COURT_TYPE)
              .operator(Operator.AND));
    }
    if (params.getLegalEffect() != null) {
      query.must(
          matchQuery(
                  CaseLawDocumentationUnit.Fields.LEGAL_EFFECT, params.getLegalEffect().toString())
              .operator(Operator.AND));
    }
    if (params.getType() != null) {
      queryDocumentType(params.getType(), query);
    }
    if (params.getTypeGroup() != null) {
      queryDocumentTypeGroup(params.getTypeGroup(), query);
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
