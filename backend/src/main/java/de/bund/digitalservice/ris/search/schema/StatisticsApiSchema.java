package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StatisticsApiSchema(
    @JsonProperty("legislation") StatisticsCountSchema legislation,
    @JsonProperty("case-law") StatisticsCountSchema caseLaw,
    @JsonProperty("literature") StatisticsCountSchema literature) {}
