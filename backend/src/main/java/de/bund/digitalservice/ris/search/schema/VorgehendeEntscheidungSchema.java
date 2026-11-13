package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

/** API schema for a prior decision of a case law document. */
@Builder
public record VorgehendeEntscheidungSchema(
    @RISSchema(name = "ris:referenzURI") String referenzUri,
    @RISSchema(name = "ris:dokumenttyp") String dokumenttyp,
    @RISSchema(name = "ris:entscheidungsdatum") LocalDate entscheidungsdatum,
    @RISSchema(name = "ris:dokumentnummer") String dokumentnummer,
    @RISSchema(name = "ris:aktenzeichen") String aktenzeichen,
    @RISSchema(name = "ris:gericht") GerichtSchema gericht)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.VORGEHENDE_ENTSCHEIDUNG)
  public String getType() {
    return JsonldTypes.VORGEHENDE_ENTSCHEIDUNG;
  }
}
