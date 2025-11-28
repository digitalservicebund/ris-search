package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
import java.util.List;
import org.mockito.ArgumentMatcher;

/**
 * Custom ArgumentMatcher to compare two lists of EcliCrawlerDocument objects. This matcher checks
 * for equality of the lists by comparing each EcliCrawlerDocument in order.
 */
public class EcliCrawlerDocumentsMatcher implements ArgumentMatcher<List<EcliCrawlerDocument>> {
  private final List<EcliCrawlerDocument> left;

  public EcliCrawlerDocumentsMatcher(List<EcliCrawlerDocument> left) {
    this.left = left;
  }

  @Override
  public boolean matches(List<EcliCrawlerDocument> right) {
    if (right.size() != left.size()) {
      return false;
    }
    for (int i = 0; i < right.size(); i++) {
      if (!left.get(i).equals(right.get(i))) {
        return false;
      }
    }
    return true;
  }
}
