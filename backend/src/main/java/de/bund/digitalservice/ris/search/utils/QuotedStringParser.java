package de.bund.digitalservice.ris.search.utils;

import java.util.ArrayList;
import java.util.List;

public class QuotedStringParser {
  private final String source;
  private final Result result;

  public QuotedStringParser(String source) {
    this.source = source;
    this.result = new Result(new ArrayList<>(), new ArrayList<>());
  }

  public Result parse() {
    var items = this.source.split("\"");
    // both '"a"' and '"a' result in [ "", "a" ] after the split operation,
    // which is why the former case needs to be treated separately
    boolean endsWithQuote = this.source.endsWith("\"");
    for (int evenIndex = 0; evenIndex < items.length; evenIndex += 2) {
      handleUnquotedTerm(items[evenIndex]);
      int oddIndex = evenIndex + 1;
      boolean isLast = oddIndex + 1 == items.length;
      if (isLast && !endsWithQuote) {
        handleUnquotedTerm(items[oddIndex]);
      } else if (oddIndex < items.length) {
        handleQuotedTerm(items[oddIndex]);
      }
    }
    return result;
  }

  private void handleQuotedTerm(String item) {
    String stripped = item.strip();
    if (!stripped.isEmpty()) {
      this.result.quotedTerms.add(stripped);
    }
  }

  private void handleUnquotedTerm(String item) {
    item = item.strip();
    if (!item.isEmpty()) {
      this.result.unquotedTerms.add(item);
    }
  }

  public record Result(List<String> unquotedTerms, List<String> quotedTerms) {}
}
