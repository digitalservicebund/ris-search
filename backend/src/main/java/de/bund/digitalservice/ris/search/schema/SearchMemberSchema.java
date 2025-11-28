package de.bund.digitalservice.ris.search.schema;

import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.util.List;
import lombok.Builder;

/**
 * Represents a structured schema for members in a search result, combining a search item and
 * associated text matches for specific fields. This schema allows for clearly encapsulating the
 * primary search result item and the corresponding metadata about text match highlights.
 *
 * <p>This class is annotated to support JSON-LD (JSON Linked Data) representation, specifically
 * typed as "SearchResult". It supports generic types to allow flexibility in representing various
 * document types that conform to the AbstractDocumentSchema.
 *
 * @param <T> The type of the document schema, extending the AbstractDocumentSchema. This parameter
 *     defines the structure of the primary search item, which could include types such as case law,
 *     legislative works, literature, or administrative directives.
 *     <p>Fields: - item: Represents the primary search result item, which adheres to the structure
 *     of the specified document schema type. - textMatches: A list of TextMatchSchema objects
 *     containing details about text matches within the search result, including matched fields and
 *     matched text snippets.
 */
@Builder
@JsonldResource
@JsonldType("SearchResult")
public record SearchMemberSchema<T extends AbstractDocumentSchema>(
    T item, List<TextMatchSchema> textMatches) {}
