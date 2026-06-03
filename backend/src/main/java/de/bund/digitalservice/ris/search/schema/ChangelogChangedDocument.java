package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/** A DTO for a single document in a ChangelogResponse. */
public record ChangelogChangedDocument(
    @JsonProperty("@id")
        @Schema(
            description = "unique identifier of the document",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @Schema(description = "type of the document", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("@type")
        String type,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String contentUrl) {}
