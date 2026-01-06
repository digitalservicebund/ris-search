package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/** A DTO for partial collection views, following schema.org naming guidelines. */
@Builder
public record PartialCollectionViewSchema(
    @Nullable String first, @Nullable String previous, @Nullable String next, @Nullable String last)
    implements JsonldResource {

  @Override
  @Schema(example = "hydra:PartialCollectionView")
  public String getType() {
    return "hydra:PartialCollectionView";
  }
}
