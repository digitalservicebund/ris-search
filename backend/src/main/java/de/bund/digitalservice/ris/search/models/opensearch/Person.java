package de.bund.digitalservice.ris.search.models.opensearch;

import javax.annotation.Nullable;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Represents a Person in a literature document, e.g. a author or collaborator. */
@Builder
public record Person(
    @Field(name = "name") String name, @Nullable @Field(name = "title") String title) {}
