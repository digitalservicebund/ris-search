package de.bund.digitalservice.ris.search.models.opensearch;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Represents a dependent reference (unselbständige Fundstelle) in a literature document. */
@Builder
public record DependentReference(
    @Field(name = "periodical") String periodical, @Field(name = "citation") String citation) {}
