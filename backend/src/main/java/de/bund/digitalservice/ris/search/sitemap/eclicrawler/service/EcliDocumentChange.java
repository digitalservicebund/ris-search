package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliSitemapMetadata;

public record EcliDocumentChange(EcliSitemapMetadata metadata, ChangeType type) {
  public enum ChangeType {
    CHANGE,
    DELETE
  }
}
