package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliSitemapMetadata;

public record DeletedDocument(EcliSitemapMetadata metadata) implements EcliDocumentChange {}
