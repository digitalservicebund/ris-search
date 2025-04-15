package de.bund.digitalservice.ris.search.service.helper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import java.util.stream.Stream;

public class FetchSourceFilterDefinitions {
  private FetchSourceFilterDefinitions() {}

  public static final List<String> CASE_LAW_FETCH_EXCLUDED_FIELDS =
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

  public static final List<String> NORMS_FETCH_EXCLUDED_FIELDS =
      List.of(
          Norm.Fields.ARTICLE_NAMES,
          Norm.Fields.ARTICLE_TEXTS,
          Norm.Fields.ARTICLES,
          Norm.Fields.TABLE_OF_CONTENTS);

  public static List<String> getDocumentExcludedFields() {
    return Stream.concat(
            CASE_LAW_FETCH_EXCLUDED_FIELDS.stream(), NORMS_FETCH_EXCLUDED_FIELDS.stream())
        .toList();
  }
}
