package de.bund.digitalservice.ris.search.unit.service.eclicrawler;

import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import java.util.List;
import java.util.Objects;
import org.mockito.ArgumentMatcher;

/**
 * Custom ArgumentMatcher to compare two lists of Url objects. This matcher checks for equality of
 * the lists by comparing specific fields within each Url object in order.
 */
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

      if (Objects.isNull(right.get(i).getDocument().getStatus())
          && Objects.isNull(left.get(i).getDocument().getStatus())) {
        return true;
      }

      if (Objects.isNull(right.get(i).getDocument().getStatus())
          || Objects.isNull(left.get(i).getDocument().getStatus())) {
        return false;
      }

      if (right.get(i).getDocument().getStatus().equals(left.get(i).getDocument().getStatus())) {
        return false;
      }
    }
    return true;
  }
}
