package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.service.helper.QuotableSearchTerm;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuotedStringParserTest {

  @Test
  void testEmptyString() {
    QuotableSearchTerm result = QuotableSearchTerm.parse("");

    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertTrue(result.quotedSearchPhrases().isEmpty());
  }

  @Test
  void testOnlyUnquotedTerms() {
    QuotableSearchTerm result = QuotableSearchTerm.parse("hello world");

    assertEquals(List.of("hello world"), result.unquotedSearchTerms());
    assertTrue(result.quotedSearchPhrases().isEmpty());
  }

  @Test
  void testOnlyQuotedTerms() {
    QuotableSearchTerm result = QuotableSearchTerm.parse("\"hello world\"");
    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertEquals(List.of("hello world"), result.quotedSearchPhrases());
  }

  @Test
  void testMixedQuotedAndUnquotedTerms() {
    QuotableSearchTerm result =
        QuotableSearchTerm.parse("hello \"quoted world\" unquoted \"another quote\"");

    assertEquals(List.of("hello", "unquoted"), result.unquotedSearchTerms());
    assertEquals(List.of("quoted world", "another quote"), result.quotedSearchPhrases());
  }

  @Test
  void testConsecutiveQuotes() {
    QuotableSearchTerm result = QuotableSearchTerm.parse("\"first quote\"\"second quote\"");

    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedSearchPhrases());
  }

  @Test
  void testConsecutiveQuotesWithWhitespace() {
    QuotableSearchTerm result = QuotableSearchTerm.parse(" \"first quote\" \"second quote\"");

    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedSearchPhrases());
  }

  @Test
  void testUnbalancedQuotes() {
    QuotableSearchTerm result = QuotableSearchTerm.parse("unbalanced \"quote");

    assertEquals(List.of("unbalanced  quote"), result.unquotedSearchTerms());
    assertEquals(List.of(), result.quotedSearchPhrases());
  }

  @Test
  void testUnbalancedQuotesWithBalancedQuotes() {
    QuotableSearchTerm result =
        QuotableSearchTerm.parse("\"balanced quote\" unbalanced \"quote text");

    assertEquals(List.of("balanced quote"), result.quotedSearchPhrases());
    assertEquals(List.of("unbalanced  quote text"), result.unquotedSearchTerms());
  }

  @Test
  void testWhitespaceHandling() {
    QuotableSearchTerm result =
        QuotableSearchTerm.parse("  leading space \"  quoted with space  \" trailing space  ");

    assertEquals(List.of("leading space", "trailing space"), result.unquotedSearchTerms());
    assertEquals(List.of("quoted with space"), result.quotedSearchPhrases());
  }

  @Test
  void testSpecialWhitespaceHandling() {
    // doesn't handle \x0B or zero-width space
    QuotableSearchTerm result =
        QuotableSearchTerm.parse("\t leading space\"quoted with space  \" trailing\nspace ");

    assertEquals(List.of("leading space", "trailing\nspace"), result.unquotedSearchTerms());
    assertEquals(List.of("quoted with space"), result.quotedSearchPhrases());
  }

  @Test
  void testEmptyQuotes() {
    QuotableSearchTerm result = QuotableSearchTerm.parse("before \"\" after");

    assertEquals(List.of("before", "after"), result.unquotedSearchTerms());
    assertEquals(List.of(), result.quotedSearchPhrases());
  }
}
