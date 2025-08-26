package de.bund.digitalservice.ris.search.models;

import java.util.List;

public record ParsedSearchTerm(
    String original, List<String> unquotedTokens, List<String> quotedSearchPhrases) {}
