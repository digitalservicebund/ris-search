package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.models.ParsedSearchTerm;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.indices.AnalyzeRequest;
import org.opensearch.client.indices.AnalyzeResponse;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
public class SearchTermService {
  private final ElasticsearchOperations elasticsearchOperations;

  public SearchTermService(ElasticsearchOperations elasticsearchOperations) {
    this.elasticsearchOperations = elasticsearchOperations;
  }

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
    // our OTC setup doesn't allow a global analyzer definition, but we use multiple copies of the
    // exact same analyzer. Therefore,  it doesn't matter which index we call.
    AnalyzeRequest request =
        AnalyzeRequest.withIndexAnalyzer("norms", "custom_german_analyzer", textToTokenize);

    OpenSearchRestTemplate osrt = (OpenSearchRestTemplate) elasticsearchOperations;
    AnalyzeResponse response =
        osrt.execute(client -> client.indices().analyze(request, RequestOptions.DEFAULT));
    return response.getTokens().stream().map(AnalyzeResponse.AnalyzeToken::getTerm).toList();
  }
}
