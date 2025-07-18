package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;

public record CreatedDocument(CaseLawDocumentationUnit docUnit) implements ChangedDocument {}
