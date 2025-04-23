package de.bund.digitalservice.ris.search.api.schema;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.util.List;
import lombok.Builder;

@Builder
@JsonldResource
@JsonldType("hydra:Collection")
public record CollectionSchema<T>(
    @JsonldId @Schema(example = ApiConfig.Paths.DOCUMENT + "?pageIndex=0&size=5") String id,
    @Schema(example = "1") long totalItems,
    List<T> member,
    PartialCollectionViewSchema view) {}
