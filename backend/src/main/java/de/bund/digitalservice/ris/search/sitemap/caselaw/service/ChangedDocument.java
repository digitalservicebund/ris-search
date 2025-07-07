package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;

public record ChangedDocument(String status, CaseLawDocumentationUnit document) {
  public static final String CHANGED = "changed";
  public static final String DELETED = "deleted";
}
