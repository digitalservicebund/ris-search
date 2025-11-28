package de.bund.digitalservice.ris.search.models.opensearch;

import java.util.List;
import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * Represents an item in a table of contents. This record is typically used to model hierarchical
 * structures where each item may contain child items, forming a tree structure.
 *
 * @param id A unique identifier for the table of contents item.
 * @param marker A textual marker associated with the item, often used to link or refer to different
 *     sections.
 * @param heading The heading or label of the item in the table of contents.
 * @param children A list of child items under the current item, allowing the representation of
 *     nested structures.
 */
@Builder
public record TableOfContentsItem(
    @Field(name = "id") String id,
    @Field(name = "marker") String marker,
    @Field(name = "heading") String heading,
    @Field(name = "children") List<TableOfContentsItem> children) {}
