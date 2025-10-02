package de.bund.digitalservice.ris.search.utils;

import static org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.BoundaryScannerType;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/** Utility class for returningHighlightBuilder for certain fields using configuration parameters */
public final class RisHighlightBuilder {

  public static final int HIGHLIGHT_FRAGMENT_SIZE = 320;
  public static final int NO_MATCH_SIZE = 160;
  public static final String HIGHLIGHT_PRE_TAG = "<mark>";
  public static final String HIGHLIGHT_POST_TAG = "</mark>";

  private RisHighlightBuilder() {}

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

  private static HighlightBuilder addLiteratureFields(HighlightBuilder builder) {
    return builder.field(Literature.Fields.MAIN_TITLE);
  }

  public static HighlightBuilder getNormsHighlighter() {
    return addNormsFields(baseHighlighter());
  }

  public static HighlightBuilder getCaseLawHighlighter() {
    return addCaseLawFields(baseHighlighter());
  }

  public static HighlightBuilder getLiteratureHighlighter() {
    return addLiteratureFields(baseHighlighter());
  }
}
