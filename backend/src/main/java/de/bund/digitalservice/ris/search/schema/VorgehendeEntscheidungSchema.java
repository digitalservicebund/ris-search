package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
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
    implements MultiTypedJsonldResource {

  @Override
  @ArraySchema(
      schema = @Schema(allowableValues = {JsonldTypes.VERWEIS, JsonldTypes.RECHTSPRECHUNG}))
  public List<String> getType() {
    return List.of(JsonldTypes.VERWEIS, JsonldTypes.RECHTSPRECHUNG);
  }
}
