package de.bund.digitalservice.ris.search.models;

import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record Attachment(
    @Nullable String marker,
    @Nullable String docTitle,
    String eId,
    String textContent,
    String manifestationEli,
    @Nullable String officialFootNotes) {}
