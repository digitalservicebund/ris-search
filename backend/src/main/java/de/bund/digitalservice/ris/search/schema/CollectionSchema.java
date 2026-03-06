package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

/** A DTO for collections of resources, following schema.org naming guidelines. */
@Builder
public record CollectionSchema<T>(
    @JsonProperty("@id")
        @Schema(
            example = ApiConfig.Paths.DOCUMENT + "?pageIndex=0&size=5",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED) long totalItems,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<T> member,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PartialCollectionViewSchema view)
    implements JsonldResource {

  @Override
  @Schema(example = "hydra:Collection")
  public String getType() {
    return "hydra:Collection";
  }
}
