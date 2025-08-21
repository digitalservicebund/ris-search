package de.bund.digitalservice.ris.search.models.opensearch;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Represents a dependent reference (selbstständige Fundstelle) in a literature document. */
@Builder
public record IndependentReference(
    @Field(name = "title") String title, @Field(name = "citation") String citation) {}
