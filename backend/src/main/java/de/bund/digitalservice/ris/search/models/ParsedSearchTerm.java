package de.bund.digitalservice.ris.search.models;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public record ParsedSearchTerm(
    String original, List<String> unquotedSearchTerms, List<String> quotedSearchPhrases) {

  public static ParsedSearchTerm parse(String searchTerm) {
    List<String> unquotedSearchTerms = new ArrayList<>();
    List<String> quotedSearchPhrases = new ArrayList<>();

    // If " is unbalanced remove the last one
    if (StringUtils.countMatches(searchTerm, "\"") % 2 == 1) {
      int lastQuoteIndex = searchTerm.lastIndexOf("\"");
      StringBuilder sb = new StringBuilder(searchTerm);
      sb.setCharAt(lastQuoteIndex, ' ');
      searchTerm = sb.toString();
    }

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
    return new ParsedSearchTerm(searchTerm, unquotedSearchTerms, quotedSearchPhrases);
  }
}
