package de.bund.digitalservice.ris.search.integration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bund.digitalservice.ris.search.integration.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import de.bund.digitalservice.ris.search.service.SearchTermService;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("integration")
class SearchTermServiceTest extends ContainersIntegrationBase {

  @Autowired SearchTermService searchTermService;

  @Test
  void testEmptyString() {
    ParsedSearchTerm result = searchTermService.parse("");

    assertTrue(result.unquotedTokens().isEmpty());
    assertTrue(result.quotedSearchPhrases().isEmpty());
  }

  @Test
  void testOnlyUnquotedTerms() {
    ParsedSearchTerm result = searchTermService.parse("hello world");

    assertEquals(List.of("hello", "world"), result.unquotedTokens());
    assertTrue(result.quotedSearchPhrases().isEmpty());
  }

  @Test
  void testOnlyQuotedTerms() {
    ParsedSearchTerm result = searchTermService.parse("\"hello world\"");
    assertTrue(result.unquotedTokens().isEmpty());
    assertEquals(List.of("hello world"), result.quotedSearchPhrases());
  }

  @Test
  void testMixedQuotedAndUnquotedTerms() {
    ParsedSearchTerm result =
        searchTermService.parse("hello \"quoted world\" unquoted \"another quote\"");

    assertEquals(List.of("hello", "unquoted"), result.unquotedTokens());
    assertEquals(List.of("quoted world", "another quote"), result.quotedSearchPhrases());
  }

  @Test
  void testConsecutiveQuotes() {
    ParsedSearchTerm result = searchTermService.parse("\"first quote\"\"second quote\"");

    assertTrue(result.unquotedTokens().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedSearchPhrases());
  }

  @Test
  void testConsecutiveQuotesWithWhitespace() {
    ParsedSearchTerm result = searchTermService.parse(" \"first quote\" \"second quote\"");

    assertTrue(result.unquotedTokens().isEmpty());
    assertEquals(List.of("first quote", "second quote"), result.quotedSearchPhrases());
  }

  @Test
  void testUnbalancedQuotes() {
    ParsedSearchTerm result = searchTermService.parse("unbalanced \"quote");

    assertEquals(List.of("unbalanced", "quote"), result.unquotedTokens());
    assertEquals(List.of(), result.quotedSearchPhrases());
  }

  @Test
  void testUnbalancedQuotesWithBalancedQuotes() {
    ParsedSearchTerm result = searchTermService.parse("\"balanced quote\" unbalanced \"quote text");

    assertEquals(List.of("balanced quote"), result.quotedSearchPhrases());
    // Stemming applied to tokens
    assertEquals(List.of("unbalanced", "quote", "text"), result.unquotedTokens());
  }

  @Test
  void testWhitespaceHandling() {
    ParsedSearchTerm result =
        searchTermService.parse("  leading space \"  quoted with space  \" trailing space  ");

    // Stemming applied to tokens
    assertEquals(List.of("leading", "space", "trailing", "space"), result.unquotedTokens());
    assertEquals(List.of("quoted with space"), result.quotedSearchPhrases());
  }

  @Test
  void testSpecialWhitespaceHandling() {
    // doesn't handle \x0B or zero-width space
    ParsedSearchTerm result =
        searchTermService.parse("\t leading space\"quoted with space  \" trailing\nspace ");

    // Stemming applied to tokens
    assertEquals(List.of("leading", "space", "trailing", "space"), result.unquotedTokens());
    assertEquals(List.of("quoted with space"), result.quotedSearchPhrases());
  }

  @Test
  void testEmptyQuotes() {
    ParsedSearchTerm result = searchTermService.parse("before \"\" after");

    // Stemming applied to tokens
    assertEquals(List.of("before", "after"), result.unquotedTokens());
    assertEquals(List.of(), result.quotedSearchPhrases());
  }
}
