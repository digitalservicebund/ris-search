package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for where a cited document was published. */
@Builder
public record FundstelleSchema(
    @RISSchema(name = "ris:periodikum") String periodikum,
    @RISSchema(name = "ris:zitatstelle") String zitatstelle,
    @RISSchema(name = "ris:fundstellenTyp") String fundstellenTyp)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.FUNDSTELLE)
  public String getType() {
    return JsonldTypes.FUNDSTELLE;
  }
}
