package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * A schema definition for representing a part of a legislative expression, adhering to schema.org
 * guidelines. This record encapsulates details about specific components of a legislative
 * expression, including metadata such as unique identifiers, names, validity dates, and optional
 * encoding information.
 *
 * <p>The legislation expression part schema helps to uniquely identify and describe specific
 * elements within a broader legislative context.
 */
@Builder
@Schema(description = "A specific part of a legislation expression")
public record LegislationExpressionPartSchema(
    @JsonProperty("@id")
        @Schema(
            example =
                ApiConfig.Paths.LEGISLATION
                    + "/eli/bund/bgbl-1/1975/s1760/regelungstext-1.xml#hauptteitel-para-1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @Schema(
            description =
                "Expression-level identifier, uniquely identifying this element in an FRBR expression",
            example = "hauptteitel-para-1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String eId,
    @Schema(example = "§ 1", requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(example = "Zulassungsvorraussetzung", requiredMode = Schema.RequiredMode.REQUIRED)
        String alternativeName,
    @Schema(
            description =
                """
                             Textual string indicating a time period in [ISO 8601 time interval format](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals)
                             """,
            example = "1998-02-06/..",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String temporalCoverage,
    @Nullable @Schema(description = "The source data for this part, if available on its own")
        List<LegislationObjectSchema> encoding,
    @ArraySchema(schema = @Schema(implementation = LegislationExpressionPartSchema.class))
        List<LegislationExpressionPartSchema> hasPart)
    implements JsonldResource {

  @Override
  @Schema(example = "Legislation")
  public String getType() {
    return "Legislation";
  }
}
