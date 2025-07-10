package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.CaseLawLdml;

public record ChangedDocument(String status, CaseLawLdml document) {
  public static final String CHANGED = "changed";
  public static final String DELETED = "deleted";
}
