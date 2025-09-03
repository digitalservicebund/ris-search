package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliSitemapMetadata;
import java.util.Objects;

public record EcliDocumentChange(EcliSitemapMetadata metadata, ChangeType type) {
  public enum ChangeType {
    CHANGE,
    DELETE
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EcliDocumentChange that = (EcliDocumentChange) o;
    return type == that.type && Objects.equals(metadata.getId(), that.metadata.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(metadata.getId(), type);
  }
}
