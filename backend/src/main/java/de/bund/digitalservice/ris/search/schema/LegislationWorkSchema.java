package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.jetbrains.annotations.Nullable;

/**
 * LegislationWork an Expression is based on
 *
 * @param legislationIdentifier
 */
public record LegislationWorkSchema(
    @Schema(example = "eli/bund/bgbl-1/1975/s1760") String legislationIdentifier,
    @Schema(
            example = "2003-12-15",
            description =
                """
                                Ausfertigungsdatum (The date of adoption or signature of the legislation. This is the date at which the text is officially acknowledged to be a legislation, even though it might not even be published or in force.)
                                """,
            requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate legislationDate,
    @Schema(
            example = "2003-12-16",
            description =
                """
                                Verkündungsdatum (The date of first publication of the legislation, when it was published in the official gazette. This may be later than the `legislationDate`.)
                                """,
            requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate datePublished,
    @Nullable PublicationIssueSchema isPartOf)
    implements JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
