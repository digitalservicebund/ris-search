package de.bund.digitalservice.ris.search.schema;

import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
@JsonldResource
@JsonldType("hydra:PartialCollectionView")
public record PartialCollectionViewSchema(
    @Nullable String first,
    @Nullable String previous,
    @Nullable String next,
    @Nullable String last) {}
