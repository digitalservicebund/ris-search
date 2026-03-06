package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the statistical data available in the API for different types of documents. This
 * schema aggregates statistical counts for legislation, case law, and literature.
 *
 * <p>Each type of document group is encapsulated within a {@link StatisticsCountSchema}, which
 * provides the count of available items in that category.
 *
 * <p>The fields in this record are annotated with {@code @JsonProperty} to ensure proper mapping to
 * and from JSON when interacting with the API.
 *
 * <p>- legislation: Statistical count for legislation items. - case-law: Statistical count for case
 * law items. - literature: Statistical count for literature items.
 */
public record StatisticsApiSchema(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("legislation")
        StatisticsCountSchema legislation,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("case-law")
        StatisticsCountSchema caseLaw,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("literature")
        StatisticsCountSchema literature,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @JsonProperty("administrative-directive")
        StatisticsCountSchema administrativeDirective) {}
