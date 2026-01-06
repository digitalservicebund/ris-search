package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.annotation.Nullable;
import lombok.Builder;

/**
 * Represents a match of text in a search result. It provides the name of the field where the match
 * occurred, the matching text snippet, and optionally, the location of the match within the field.
 *
 * <p>This class is annotated to support JSON-LD (JSON Linked Data) representation, specifically
 * typed as "SearchResultMatch". It is used to represent the text matching results in a structured
 * and semantic way.
 *
 * <p>Fields: - name: Specifies the name of the field where the text was matched. - text: Contains
 * the actual matched text snippet. - location: Indicates the location or context of the matched
 * text, if available.
 */
@Builder
public record TextMatchSchema(String name, String text, @Nullable String location)
    implements JsonldResource {

  @Override
  @Schema(example = "SearchResultMatch")
  public String getType() {
    return "SearchResultMatch";
  }
}
