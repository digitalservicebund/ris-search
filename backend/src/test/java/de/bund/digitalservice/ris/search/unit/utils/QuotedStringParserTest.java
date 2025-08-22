package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuotedStringParserTest {

  @Test
  void testEmptyString() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("");

    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertTrue(result.quotedSearchPhrases().isEmpty());
  }

  @Test
  void testOnlyUnquotedTerms() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("hello world");

    assertEquals(List.of("hello world"), result.unquotedSearchTerms());
    assertTrue(result.quotedSearchPhrases().isEmpty());
  }

  @Test
  void testOnlyQuotedTerms() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("\"hello world\"");
    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertEquals(List.of("hello world"), result.quotedSearchPhrases());
  }

  @Test
  void testMixedQuotedAndUnquotedTerms() {
    ParsedSearchTerm result =
        ParsedSearchTerm.parse("hello \"quoted world\" unquoted \"another quote\"");

    assertEquals(List.of("hello", "unquoted"), result.unquotedSearchTerms());
    assertEquals(List.of("quoted world", "another quote"), result.quotedSearchPhrases());
  }

  @Test
  void testConsecutiveQuotes() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("\"first quote\"\"second quote\"");

    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedSearchPhrases());
  }

  @Test
  void testConsecutiveQuotesWithWhitespace() {
    ParsedSearchTerm result = ParsedSearchTerm.parse(" \"first quote\" \"second quote\"");

    assertTrue(result.unquotedSearchTerms().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedSearchPhrases());
  }

  @Test
  void testUnbalancedQuotes() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("unbalanced \"quote");

    assertEquals(List.of("unbalanced  quote"), result.unquotedSearchTerms());
    assertEquals(List.of(), result.quotedSearchPhrases());
  }

  @Test
  void testUnbalancedQuotesWithBalancedQuotes() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("\"balanced quote\" unbalanced \"quote text");

    assertEquals(List.of("balanced quote"), result.quotedSearchPhrases());
    assertEquals(List.of("unbalanced  quote text"), result.unquotedSearchTerms());
  }

  @Test
  void testWhitespaceHandling() {
    ParsedSearchTerm result =
        ParsedSearchTerm.parse("  leading space \"  quoted with space  \" trailing space  ");

    assertEquals(List.of("leading space", "trailing space"), result.unquotedSearchTerms());
    assertEquals(List.of("quoted with space"), result.quotedSearchPhrases());
  }

  @Test
  void testSpecialWhitespaceHandling() {
    // doesn't handle \x0B or zero-width space
    ParsedSearchTerm result =
        ParsedSearchTerm.parse("\t leading space\"quoted with space  \" trailing\nspace ");

    assertEquals(List.of("leading space", "trailing\nspace"), result.unquotedSearchTerms());
    assertEquals(List.of("quoted with space"), result.quotedSearchPhrases());
  }

  @Test
  void testEmptyQuotes() {
    ParsedSearchTerm result = ParsedSearchTerm.parse("before \"\" after");

    assertEquals(List.of("before", "after"), result.unquotedSearchTerms());
    assertEquals(List.of(), result.quotedSearchPhrases());
  }
}
