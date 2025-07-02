package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.utils.QuotedStringParser;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuotedStringParserTest {

  @Test
  void testEmptyString() {
    QuotedStringParser parser = new QuotedStringParser("");
    QuotedStringParser.Result result = parser.parse();

    assertTrue(result.unquotedTerms().isEmpty());
    assertTrue(result.quotedTerms().isEmpty());
  }

  @Test
  void testOnlyUnquotedTerms() {
    QuotedStringParser parser = new QuotedStringParser("hello world");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("hello", "world"), result.unquotedTerms());
    assertTrue(result.quotedTerms().isEmpty());
  }

  @Test
  void testOnlyQuotedTerms() {
    QuotedStringParser parser = new QuotedStringParser("\"hello world\"");
    QuotedStringParser.Result result = parser.parse();

    assertTrue(result.unquotedTerms().isEmpty());
    assertEquals(List.of("hello world"), result.quotedTerms());
  }

  @Test
  void testMixedQuotedAndUnquotedTerms() {
    QuotedStringParser parser =
        new QuotedStringParser("hello \"quoted world\" unquoted \"another quote\"");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("hello", "unquoted"), result.unquotedTerms());
    assertEquals(List.of("quoted world", "another quote"), result.quotedTerms());
  }

  @Test
  void testConsecutiveQuotes() {
    QuotedStringParser parser = new QuotedStringParser("\"first quote\"\"second quote\"");
    QuotedStringParser.Result result = parser.parse();

    assertTrue(result.unquotedTerms().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedTerms());
  }

  @Test
  void testConsecutiveQuotesWithWhitespace() {
    QuotedStringParser parser = new QuotedStringParser(" \"first quote\" \"second quote\"");
    QuotedStringParser.Result result = parser.parse();

    assertTrue(result.unquotedTerms().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedTerms());
  }

  @Test
  void testUnbalancedQuotes() {
    QuotedStringParser parser = new QuotedStringParser("unbalanced \"quote");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("unbalanced", "quote"), result.unquotedTerms());
    assertEquals(List.of(), result.quotedTerms());
  }

  @Test
  void testUnbalancedQuotesWithBalancedQuotes() {
    QuotedStringParser parser =
        new QuotedStringParser("\"balanced quote\" unbalanced \"quote text");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("balanced quote"), result.quotedTerms());
    assertEquals(List.of("unbalanced", "quote", "text"), result.unquotedTerms());
  }

  @Test
  void testWhitespaceHandling() {
    QuotedStringParser parser =
        new QuotedStringParser("  leading space \"  quoted with space  \" trailing space  ");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("leading", "space", "trailing", "space"), result.unquotedTerms());
    assertEquals(List.of("quoted with space"), result.quotedTerms());
  }

  @Test
  void testSpecialWhitespaceHandling() {
    // doesn't handle \x0B or zero-width space
    QuotedStringParser parser =
        new QuotedStringParser("\t leading space\"quoted with space  \" trailing\nspace ");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("leading", "space", "trailing", "space"), result.unquotedTerms());
    assertEquals(List.of("quoted with space"), result.quotedTerms());
  }

  @Test
  void testEmptyQuotes() {
    QuotedStringParser parser = new QuotedStringParser("before \"\" after");
    QuotedStringParser.Result result = parser.parse();

    assertEquals(List.of("before", "after"), result.unquotedTerms());
    assertEquals(List.of(), result.quotedTerms());
  }
}
