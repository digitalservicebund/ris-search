package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the bulk download links for different document kinds.
 *
 * <p>Each document kind is encapsulated within a {@link BulkZipLinkSchema}, which provides the link
 * for that document kind.
 *
 * <p>The fields in this record are annotated with {@code @JsonProperty} to ensure proper mapping to
 * and from JSON when interacting with the API.
 */
public record BulkZipLinksSchema(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("legislation")
        BulkZipLinkSchema legislation,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("case-law")
        BulkZipLinkSchema caseLaw,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("literature")
        BulkZipLinkSchema literature,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("administrative-directive")
        BulkZipLinkSchema administrativeDirective) {}
