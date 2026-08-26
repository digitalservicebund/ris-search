package de.bund.digitalservice.ris.search.models;

import java.util.List;

/**
 * Represents a parsed search term used in search operations.
 *
 * <p>This record is used to store and process user-provided search terms. It includes the original
 * input, tokenized terms without quotation marks, and specific quoted phrases.
 *
 * @param original The original search term string as provided by the user.
 * @param unquotedTokens A list of individual tokens derived from the search term, excluding any
 *     quotes or quoted phrases.
 * @param quotedSearchPhrases A list of phrases extracted from the search term that were enclosed in
 *     quotation marks, preserving their original form.
 */
public record ParsedSearchTerm(
    String original, List<String> unquotedTokens, List<String> quotedSearchPhrases) {}
