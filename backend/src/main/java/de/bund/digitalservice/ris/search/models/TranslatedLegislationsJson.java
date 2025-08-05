package de.bund.digitalservice.ris.search.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TranslatedLegislationsJson(
    String abbreviation,
    String name,
    String filename,
    String translator,
    String version,
    @JsonProperty("german_name") String germanName) {}
