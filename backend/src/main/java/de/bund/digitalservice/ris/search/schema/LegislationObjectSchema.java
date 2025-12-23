package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** A DTO for legislation in a specific encoding, following schema.org naming guidelines. */
@Builder
public record LegislationObjectSchema(
    @JsonProperty("@type") String type,
    @JsonProperty("@id")
        @Schema(
            example =
                ApiConfig.Paths.LEGISLATION
                    + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1/html")
        String id,
    @Schema(
            example =
                ApiConfig.Paths.LEGISLATION
                    + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/1998-01-29/regelungstext-1.html")
        String contentUrl,
    @Schema(example = "text/html") String encodingFormat,
    @Schema(example = "de") String inLanguage) {

  public static class LegislationObjectSchemaBuilder {
    LegislationObjectSchemaBuilder() {
      type = "LegislationObject";
    }
  }
}
