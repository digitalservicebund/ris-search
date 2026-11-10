package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.models.Richtung;
import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for a citation of selbstständige (independent) literature. */
@Builder
public record ZitierungLiteraturSelbststaendigSchema(
    @RISSchema(name = "ris:referenzRichtung") Richtung richtung,
    @RISSchema(name = "ris:referenzURI") String referenzUri,
    @RISSchema(name = "ris:dokumentnummer") String dokumentnummer,
    @RISSchema(name = "ris:autor") String autor,
    @RISSchema(name = "ris:referenzSelbststaendigeLiteraturTitel") String titel,
    @RISSchema(name = "ris:veroeffentlichungsjahr") String veroeffentlichungsjahr)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.ZITIERUNG_LITERATUR_SELBSTSTAENDIG)
  public String getType() {
    return JsonldTypes.ZITIERUNG_LITERATUR_SELBSTSTAENDIG;
  }
}
