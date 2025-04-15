package de.bund.digitalservice.ris.search.models;

import lombok.Builder;

@Builder
public record Attachment(
    String marker, String docTitle, String eId, String textContent, String manifestationEli) {}
