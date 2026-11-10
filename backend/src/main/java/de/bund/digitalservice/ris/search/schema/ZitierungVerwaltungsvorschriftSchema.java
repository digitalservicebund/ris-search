package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for a citation of an administrative directive (Verwaltungsvorschrift). */
@Builder
public record ZitierungVerwaltungsvorschriftSchema(
    @RISSchema(name = "ris:richtung") ZitierungRichtung richtung,
    @RISSchema(name = "ris:referenzURI") String referenzUri,
    @RISSchema(name = "ris:dokumentnummer") String dokumentnummer,
    @RISSchema(name = "ris:artDerZitierung") String artDerZitierung,
    @RISSchema(name = "ris:jurisAbkuerzung") String jurisAbkuerzung,
    @RISSchema(name = "ris:fundstelle") FundstelleSchema fundstelle)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.ZITIERUNG_VERWALTUNGSVORSCHRIFT)
  public String getType() {
    return JsonldTypes.ZITIERUNG_VERWALTUNGSVORSCHRIFT;
  }
}
