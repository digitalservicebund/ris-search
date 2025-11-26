package de.bund.digitalservice.ris.search.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * Represents translated legislation metadata as a record.
 *
 * <p>This class encapsulates attributes related to translated legislations, including their
 * abbreviation, name, filename, translator, version, and the German name. It ensures that the
 * associated fields are immutable and clearly defines the structure for JSON serialization.
 *
 * @param abbreviation The abbreviation of the legislation.
 * @param name The name of the legislation.
 * @param filename The filename associated with the legislation.
 * @param translator The name of the translator for the legislation.
 * @param version The version of the translated legislation.
 * @param germanName The name of the legislation in German.
 */
@Builder
public record TranslatedLegislationsJson(
    String abbreviation,
    String name,
    String filename,
    String translator,
    String version,
    @JsonProperty("german_name") String germanName) {}
