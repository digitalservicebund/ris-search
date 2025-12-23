package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A record representing a minimal <a href="https://schema.org/PublicationIssue">schema.org
 * PublicationIssue</a> type
 */
public record PublicationIssueSchema(
    @JsonProperty("@type") String type, @Schema(example = "BGBL I 2003, 1760") String name) {

  public PublicationIssueSchema(String name) {
    this("PublicationIssue", name);
  }
}
