package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for a citation of unselbstständige (dependent) literature. */
@Builder
public record ZitierungLiteraturUnselbststaendigSchema(
    @RISSchema(name = "ris:referenzRichtung") String richtung,
    @RISSchema(name = "ris:referenzURI") String referenzUri,
    @RISSchema(name = "ris:dokumentnummer") String dokumentnummer,
    @RISSchema(name = "ris:autor") String autor,
    @RISSchema(name = "ris:dokumenttyp") String dokumenttyp,
    @RISSchema(name = "ris:fundstelle") FundstelleSchema fundstelle)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.ZITIERUNG_LITERATUR_UNSELBSTSTAENDIG)
  public String getType() {
    return JsonldTypes.ZITIERUNG_LITERATUR_UNSELBSTSTAENDIG;
  }
}
