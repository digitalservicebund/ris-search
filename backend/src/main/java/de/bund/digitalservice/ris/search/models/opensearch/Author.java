package de.bund.digitalservice.ris.search.models.opensearch;

import javax.annotation.Nullable;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/** Represents on author (Verfasser) of a Literature work. */
@Builder
public record Author(
    @Field(name = "name") String name, @Nullable @Field(name = "title") String title) {}
