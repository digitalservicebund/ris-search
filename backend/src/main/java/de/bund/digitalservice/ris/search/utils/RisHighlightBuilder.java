package de.bund.digitalservice.ris.search.utils;

import static org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.BoundaryScannerType;
import static org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.Field;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import java.util.List;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Utility class for returningHighlightBuilder for certain fields using configuration parameters */
public final class RisHighlightBuilder {
  public static final List<String> CASE_LAW_HIGHLIGHT_CONTENT_FIELDS =
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

  public static final int HIGHLIGHT_FRAGMENT_SIZE = 320;
  public static final int NO_MATCH_SIZE = 160;
  public static final String HIGHLIGHT_PRE_TAG = "<mark>";
  public static final String HIGHLIGHT_POST_TAG = "</mark>";

  private RisHighlightBuilder() {}

  /**
   * Adds a field fetch directive that excludes noMatch content, for metadata fields such as ecli
   */
  private static Field getFieldBasic(String name) {
    return new Field(name).noMatchSize(0);
  }

  public static HighlightBuilder baseHighlighter() {
    return new HighlightBuilder()
        .fragmentSize(HIGHLIGHT_FRAGMENT_SIZE)
        .boundaryScannerType(BoundaryScannerType.SENTENCE)
        .numOfFragments(1)
        .preTags(HIGHLIGHT_PRE_TAG)
        .postTags(HIGHLIGHT_POST_TAG)
        .noMatchSize(NO_MATCH_SIZE);
  }

  public static HighlightBuilder getArticleFieldsHighlighter() {
    return baseHighlighter().field("articles.text").field("articles.name");
  }

  public static HighlightBuilder getUniversalHighlighter() {
    var builder = baseHighlighter();
    addCaseLawFields(builder);
    addNormsFields(builder);
    return builder;
  }

  private static HighlightBuilder addNormsFields(HighlightBuilder builder) {
    return builder.field(Norm.Fields.OFFICIAL_TITLE);
  }

  private static HighlightBuilder addCaseLawFields(HighlightBuilder builder) {
    CASE_LAW_HIGHLIGHT_CONTENT_FIELDS.forEach(builder::field);
    builder.field(getFieldBasic(CaseLawDocumentationUnit.Fields.ECLI));
    builder.field(getFieldBasic(CaseLawDocumentationUnit.Fields.FILE_NUMBERS));
    return builder;
  }

  public static HighlightBuilder getNormsHighlighter() {
    return addNormsFields(baseHighlighter());
  }

  public static HighlightBuilder getCaseLawHighlighter() {
    return addCaseLawFields(baseHighlighter());
  }
}
