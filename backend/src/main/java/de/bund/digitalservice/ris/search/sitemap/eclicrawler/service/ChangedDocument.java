package de.bund.digitalservice.ris.search.sitemap.eclicrawler.service;

public sealed interface ChangedDocument permits CreatedDocument, DeletedDocument {}
