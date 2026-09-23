package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * A schema definition for representing an article a legislative expression
 *
 * <p>The schema helps to uniquely identify a unique article across it's versions and the
 * expressions each version is part of.
 */
@Builder
@Schema(description = "A specific part of a legislation expression")
public record ArticleVersionSchema(
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
    @Schema(
            description = "Numerical identifier of a specific legislation part",
            example = "§ 1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(
            description =
                """
                             Textual string indicating a time period in [ISO 8601 time interval format](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals)
                             """,
            example = "1998-02-06/..",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String temporalCoverage,
    @Nullable
        @Schema(
            description =
                """
                        Specifies the type of the part of a Legislation Expression.
                    """)
        LegislationExpressionPartType partType,
    @Nullable @Schema(description = "The source data for this part, if available on its own")
        List<LegislationObjectSchema> encoding,
    @ArraySchema(schema = @Schema(implementation = ArticleVersionSchema.IsPartOfReference.class))
        List<ArticleVersionSchema.IsPartOfReference> isPartOf)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.LEGISLATION)
  public String getType() {
    return JsonldTypes.LEGISLATION;
  }

  /**
   * Legislation Reference that is a parent of an article version
   *
   * @param id
   */
  @Schema(description = "A reference to another expression that the article is part of")
  public record IsPartOfReference(
      @JsonProperty("@id")
          @Schema(
              example =
                  ApiConfig.Paths.LEGISLATION
                      + "/eli/bund/bgbl-1/1975/s1760/1998-01-29/10/deu/art-z1",
              requiredMode = Schema.RequiredMode.REQUIRED)
          String id)
      implements JsonldResource {
    @Override
    public String getType() {
      return JsonldTypes.LEGISLATION;
    }

    public static IsPartOfReference fromExpressionEli(String expressionEli) {
      return new IsPartOfReference(ApiConfig.Paths.LEGISLATION + "/" + expressionEli);
    }
  }
}
