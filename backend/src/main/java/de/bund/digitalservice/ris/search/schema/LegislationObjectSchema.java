package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** A DTO for legislation in a specific encoding, following schema.org naming guidelines. */
@Builder
public record LegislationObjectSchema(
    @JsonProperty("@id")
        @Schema(
            example =
                ApiConfig.Paths.LEGISLATION
                    + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1/html",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @Schema(
            example =
                ApiConfig.Paths.LEGISLATION
                    + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1.html",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String contentUrl,
    @Schema(example = "text/html", requiredMode = Schema.RequiredMode.REQUIRED)
        String encodingFormat,
    @Schema(example = "de", requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage)
    implements JsonldResource {

  @Override
  @Schema(example = "LegislationObject")
  public String getType() {
    return "LegislationObject";
  }
}
