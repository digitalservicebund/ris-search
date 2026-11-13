package de.bund.digitalservice.ris.search.schema;

import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** API schema for the periodical a cited document was published in. */
@Builder
public record PeriodikumSchema(
    @RISSchema(name = "ris:periodikumAbkuerzung") String abkuerzung,
    @RISSchema(name = "ris:periodikumTitel") String titel)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.PERIODIKUM)
  public String getType() {
    return JsonldTypes.PERIODIKUM;
  }
}
