package de.bund.digitalservice.ris.search.models.opensearch;

import java.util.List;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

@Builder
public record TableOfContentsItem(
    @Field(name = "id") String id,
    @Field(name = "marker") String marker,
    @Field(name = "heading") String heading,
    @Field(name = "children") List<TableOfContentsItem> children) {}
