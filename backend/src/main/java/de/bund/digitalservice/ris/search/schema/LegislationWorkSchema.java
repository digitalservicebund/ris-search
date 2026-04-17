package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.jetbrains.annotations.Nullable;

/**
 * LegislationWork an Expression is based on
 *
 * @param legislationIdentifier
 */
public record LegislationWorkSchema(
    @JsonProperty("@id")
        @Schema(
            example = ApiConfig.Paths.LEGISLATION + "/eli/bund/bgbl-1/1975",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @Schema(example = "eli/bund/bgbl-1/1975/s1760", requiredMode = Schema.RequiredMode.REQUIRED)
        String legislationIdentifier,
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
