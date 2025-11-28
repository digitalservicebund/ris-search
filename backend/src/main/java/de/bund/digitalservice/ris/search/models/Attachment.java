package de.bund.digitalservice.ris.search.models;

import lombok.Builder;
import org.springframework.lang.Nullable;

/**
 * Represents an attachment entity with various attributes.
 *
 * <p>This class serves as a record to encapsulate information about an attachment, such as its
 * marker, document title, electronic identifier, text content, manifestation ELI (European
 * Legislation Identifier), and any associated official footnotes.
 *
 * <p>All fields are immutable and can be null unless explicitly specified otherwise.
 *
 * @param marker An optional marker associated with the attachment.
 * @param docTitle An optional title of the document associated with the attachment.
 * @param eId The electronic identifier for the attachment.
 * @param textContent The textual content of the attachment.
 * @param manifestationEli The manifestation ELI (European Legislation Identifier) for the
 *     attachment.
 * @param officialFootNotes Optional official footnotes related to the attachment.
 */
@Builder
public record Attachment(
    @Nullable String marker,
    @Nullable String docTitle,
    String eId,
    String textContent,
    String manifestationEli,
    @Nullable String officialFootNotes) {}
