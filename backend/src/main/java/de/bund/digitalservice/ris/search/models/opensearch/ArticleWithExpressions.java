package de.bund.digitalservice.ris.search.models.opensearch;

import java.util.List;

/**
 * Carries an {@link Article} representing one version together with the expressionElis of every
 * expression that article occurs in.
 *
 * @param article the representative article for the document number
 * @param expressionElis expressionElis of every expression the document number occurs in
 */
public record ArticleWithExpressions(Article article, List<String> expressionElis) {}
