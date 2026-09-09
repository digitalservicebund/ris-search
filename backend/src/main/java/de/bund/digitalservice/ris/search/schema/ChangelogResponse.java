package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/** A DTO for ChangelogResponses. */
public record ChangelogResponse(
    @JsonProperty("@context") @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String context,
    @Schema(description = "Set of changed documents", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<ChangelogChangedDocument> changed,
    @Schema(description = "Set of deleted documents", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<ChangelogDeletedDocument> deleted,
    @Schema(
            description = "flag to communicate that the whole storage got rebuilt",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean allChanged)
    implements JsonldResource {

  @Override
  public String getType() {
    return JsonldTypes.CHANGELOG;
  }
}
