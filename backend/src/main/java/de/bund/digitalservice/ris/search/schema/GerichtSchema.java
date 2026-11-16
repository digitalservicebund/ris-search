package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for the court of a cited decision. */
@Builder
public record GerichtSchema(
    @RISSchema(name = "ris:gerichtstyp") String gerichtstyp,
    @RISSchema(name = "ris:gerichtsort") String gerichtsort,
    @RISSchema(name = "ris:gerichtsbarkeit") String gerichtsbarkeit,
    @RISSchema(name = "ris:spruchkoerper") String spruchkoerper,
    @RISSchema(name = "ris:sitzDerAussenstelle") String sitzDerAussenstelle)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.GERICHT)
  public String getType() {
    return JsonldTypes.GERICHT;
  }
}
