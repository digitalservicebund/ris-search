package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

/** API schema for a citation of selbstständige (independent) literature. */
@Builder
public record ZitierungLiteraturSelbststaendigSchema(
    @RISSchema(name = "ris:richtung") ZitierungRichtung richtung,
    @RISSchema(name = "ris:referenzURI") String referenzUri,
    @RISSchema(name = "ris:dokumentnummer") String dokumentnummer,
    @RISSchema(name = "ris:autor") String autor,
    @RISSchema(name = "ris:referenzSelbststaendigeLiteraturTitel") String titel,
    @RISSchema(name = "ris:veroeffentlichungsjahr") String veroeffentlichungsjahr)
    implements MultiTypedJsonldResource {

  @Override
  @ArraySchema(schema = @Schema(allowableValues = {JsonldTypes.VERWEIS, JsonldTypes.LITERATURE}))
  public List<String> getType() {
    return List.of(JsonldTypes.VERWEIS, JsonldTypes.LITERATURE);
  }
}
