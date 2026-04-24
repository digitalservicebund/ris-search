package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.Set;

/** A DTO for ChangelogResponses. */
public record ChangelogResponse(
    @Schema(description = "Set of changed documents", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<ChangelogDocument> changed,
    @Schema(description = "Set of deleted documents", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<ChangelogDocument> deleted,
    @Schema(
            description = "flag to communicate that the whole storage got rebuilt",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean allChanged,
    @JsonIgnore String baseUrl) {

  @JsonProperty(value = "@context", index = 0)
  public Map<String, Object> getContext() {
    return Map.of(
        "@vocab",
        "https://schema.org/",
        "allChanged",
        Map.of("@id", baseUrl + "/namespace#allChanged", "@type", "boolean"),
        "changed",
        Map.of(
            "@id", "UpdateAction",
            "@container", "@set"),
        "deleted",
        Map.of(
            "@id", "DeleteAction",
            "@container", "@set"));
  }
}
