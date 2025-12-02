package de.bund.digitalservice.ris.search.models.sitemap;

import lombok.Getter;

/**
 * Enum representing the types of sitemaps supported in the system.
 *
 * <p>The constants define the two distinct sitemap types: - NORMS: Represents sitemaps associated
 * with legal norms. - CASELAW: Represents sitemaps related to case law documents.
 *
 * <p>This enum is intended to categorize and differentiate the content covered within various
 * sitemap files.
 */
@Getter
public enum SitemapType {
  NORMS("norms"),
  CASELAW("caselaw");

  private String path;

  private SitemapType(String path) {
    this.path = path;
  }
}
