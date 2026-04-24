package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A record representing a minimal <a href="https://schema.org/PublicationIssue">schema.org
 * PublicationIssue</a> type
 */
public record PublicationIssueSchema(
    @Schema(example = "BGBL I 2003, 1760", requiredMode = Schema.RequiredMode.REQUIRED) String name)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.PUBLICATION_ISSUE)
  public String getType() {
    return JsonldTypes.PUBLICATION_ISSUE;
  }
}
