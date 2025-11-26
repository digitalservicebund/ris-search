package de.bund.digitalservice.ris.search.utils;

import static org.opensearch.search.fetch.subphase.highlight.HighlightBuilder.BoundaryScannerType;

import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;

/**
 * Utility class for returning HighlightBuilder for certain fields using configuration parameters
 */
public final class RisHighlightBuilder {

  public static final int HIGHLIGHT_FRAGMENT_SIZE = 320;
  public static final int NO_MATCH_SIZE = 160;
  public static final String HIGHLIGHT_PRE_TAG = "<mark>";
  public static final String HIGHLIGHT_POST_TAG = "</mark>";

  private RisHighlightBuilder() {}

  /**
   * Builds and returns a pre-configured instance of HighlightBuilder with specified configuration
   * parameters. The HighlightBuilder is configured to use a fragment size, boundary scanner type,
   * number of fragments, pre-tags, post-tags, and no-match size that are predefined.
   *
   * @return a HighlightBuilder instance configured with specific highlighter settings for text
   *     highlighting
   */
  public static HighlightBuilder baseHighlighter() {
    return new HighlightBuilder()
        .fragmentSize(HIGHLIGHT_FRAGMENT_SIZE)
        .boundaryScannerType(BoundaryScannerType.SENTENCE)
        .numOfFragments(1)
        .preTags(HIGHLIGHT_PRE_TAG)
        .postTags(HIGHLIGHT_POST_TAG)
        .noMatchSize(NO_MATCH_SIZE);
  }
}
