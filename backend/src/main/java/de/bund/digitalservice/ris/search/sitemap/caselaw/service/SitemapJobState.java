package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import lombok.With;

@With
public record SitemapJobState(String lastSuccess) {
  public SitemapJobState() {
    this(null);
  }
}
