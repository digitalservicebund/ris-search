package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LegislationWork an Expression is based on
 *
 * @param legislationIdentifier
 */
public record LegislationWorkSchema(
    @Schema(example = "eli/bund/bgbl-1/1975/s1760") String legislationIdentifier)
    implements JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
