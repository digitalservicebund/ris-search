package de.bund.digitalservice.ris.search.unit.eclicrawler;

import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Url;
import java.util.List;
import org.mockito.ArgumentMatcher;

public class UrlListMatcher implements ArgumentMatcher<List<Url>> {
  private final List<Url> left;

  public UrlListMatcher(List<Url> left) {
    this.left = left;
  }

  @Override
  public boolean matches(List<Url> right) {
    if (right.size() != left.size()) {
      return false;
    }

    for (int i = 0; i < right.size(); i++) {
      if (!left.get(i)
          .getDocument()
          .getMetadata()
          .getIsVersionOf()
          .getValue()
          .equals(right.get(i).getDocument().getMetadata().getIsVersionOf().getValue())) {
        return false;
      }
    }
    return true;
  }
}
