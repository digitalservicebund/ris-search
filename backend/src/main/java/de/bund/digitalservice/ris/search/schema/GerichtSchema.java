package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for the court of a cited decision. */
@Builder
public record GerichtSchema(
    @Schema(example = "BGH") String gerichtstyp,
    @Schema(example = "Karlsruhe") String gerichtsort,
    @Schema(example = "Ordentliche Gerichtsbarkeit") String gerichtsbarkeit,
    @Schema(example = "1. Senat") String spruchkoerper,
    @Schema(example = "Nürnberg") String sitzDerAussenstelle)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.GERICHT)
  public String getType() {
    return JsonldTypes.GERICHT;
  }
}
