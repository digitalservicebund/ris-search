package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Represents the structure of a table of contents entry, following the "TocEntry" JSON-LD type.
 *
 * <p>This schema is designed to allow hierarchical representation of document sections and their
 * sub-sections. Each entry contains an identifier, a marker (e.g., numbering or bullet), a heading
 * text, and optionally, a list of child entries representing nested structure.
 *
 * <p>Fields: - id: Unique identifier for the table of contents entry. - marker: A marker or
 * numbering associated with this entry. - heading: The title or heading of the content associated
 * with this entry. - children: A list of child entries forming a hierarchical structure under this
 * entry.
 */
public record TableOfContentsSchema(
    @Schema(example = "hauptteil-1_para-1") String id,
    @Schema(example = "1") String marker,
    @Schema(example = "Art 1") String heading,
    List<TableOfContentsSchema> children,
    @JsonProperty("@type") String type) {

  public TableOfContentsSchema(
      String id, String marker, String heading, List<TableOfContentsSchema> children) {
    this(id, marker, heading, children, "TocEntry");
  }
}
