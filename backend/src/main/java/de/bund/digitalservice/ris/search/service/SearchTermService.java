package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SearchTermService {

  // This method turns searchTerm into two lists of strings. Sections inside double quotes
  // (quotedSearchPhrases) and sections outside double quotes (unquotedSearchTerms).
  // See SearchTermParserTest for examples
  public ParsedSearchTerm parse(String searchTerm) {
    List<String> unquotedSearchTerms = new ArrayList<>();
    List<String> quotedSearchPhrases = new ArrayList<>();

    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      return new ParsedSearchTerm(searchTerm, unquotedSearchTerms, quotedSearchPhrases);
    }

    searchTerm = balanceQuotes(searchTerm);

    // split up the quoted and unquoted parts
    String[] items = searchTerm.split("\"");
    for (int index = 0; index < items.length; index++) {
      String current = items[index].strip();
      if (StringUtils.isNotEmpty(current)) {
        if (index % 2 == 0) {
          unquotedSearchTerms.add(current);
        } else {
          quotedSearchPhrases.add(current);
        }
      }
    }

    String allUnquotedParts = String.join(" ", unquotedSearchTerms).strip();
    return new ParsedSearchTerm(searchTerm, tokenize(allUnquotedParts), quotedSearchPhrases);
  }

  private String balanceQuotes(String input) {
    // A search only makes sense with balanced quotes. For example, searching for
    // my "search" string"
    // doesn't make sense. Therefore, if " is unbalanced, we remove the last one
    if (StringUtils.countMatches(input, "\"") % 2 == 1) {
      int lastQuoteIndex = input.lastIndexOf("\"");
      StringBuilder sb = new StringBuilder(input);
      sb.setCharAt(lastQuoteIndex, ' ');
      return sb.toString();
    } else {
      return input;
    }
  }

  private List<String> tokenize(String textToTokenize) {
    String[] temp = textToTokenize.split("\\s+");
    List<String> tokens = new ArrayList<>();
    for (String token : temp) {
      if (StringUtils.isNotEmpty(token.strip())) {
        tokens.add(token);
      }
    }
    return tokens;
  }
}
