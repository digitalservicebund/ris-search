package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/** A DTO for partial collection views, following schema.org naming guidelines. */
@Builder
@JsonldType("hydra:PartialCollectionView")
public record PartialCollectionViewSchema(
    @JsonProperty("@type") String type,
    @Nullable String first,
    @Nullable String previous,
    @Nullable String next,
    @Nullable String last) {

  public PartialCollectionViewSchema {
    type = "hydra:PartialCollectionView";
  }
}
