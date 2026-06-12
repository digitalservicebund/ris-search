package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;

/** A schema that represents the link to the bulk zip for the given document kind. */
public record BulkZipLinkSchema(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) String link) {}
