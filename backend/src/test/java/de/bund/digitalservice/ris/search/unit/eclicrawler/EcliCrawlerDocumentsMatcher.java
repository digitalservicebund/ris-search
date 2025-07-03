package de.bund.digitalservice.ris.search.unit.eclicrawler;

import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import java.util.List;
import org.mockito.ArgumentMatcher;

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
