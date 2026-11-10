package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

/** API schema for a citation of a case law document. */
@Builder
public record ZitierungRechtsprechungSchema(
    @RISSchema(name = "ris:referenzRichtung") String richtung,
    @RISSchema(name = "ris:referenzURI") String referenzUri,
    @RISSchema(name = "ris:dokumentnummer") String dokumentnummer,
    @RISSchema(name = "ris:referenzArtDerZitierung") String artDerZitierung,
    @RISSchema(name = "ris:gericht") GerichtSchema gericht,
    @RISSchema(name = "ris:entscheidungsdatum") LocalDate entscheidungsdatum,
    @RISSchema(name = "ris:dokumenttyp") String dokumenttyp,
    @RISSchema(name = "ris:aktenzeichen") String aktenzeichen,
    @RISSchema(name = "ris:fundstelle") FundstelleSchema fundstelle)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.ZITIERUNG_RECHTSPRECHUNG)
  public String getType() {
    return JsonldTypes.ZITIERUNG_RECHTSPRECHUNG;
  }
}
