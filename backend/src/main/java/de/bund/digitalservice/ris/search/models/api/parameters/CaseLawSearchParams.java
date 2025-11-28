package de.bund.digitalservice.ris.search.models.api.parameters;

import de.bund.digitalservice.ris.search.caselawhandover.model.LegalEffect;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the search parameters for querying case law documents.
 *
 * <p>It allows filtering the search results based on multiple criteria such as file number,
 * European Case Law Identifier (ECLI), court, legal effect, document type, and type group.
 *
 * <p>Filtering Options: - `fileNumber`: Filter by the specific file number associated with the
 * case. - `ecli`: Filter by the European Case Law Identifier (ECLI). - `court`: Filter by the court
 * name (e.g., "Finanzgericht Münster") or court type (e.g., "FG Münster"). Supports both short and
 * long names. - `legalEffect`: Filter based on whether the decision is legally binding. This
 * corresponds to the concept of "Rechtskraft." - `type`: Filter by document type (e.g., "Urteil",
 * "Versäumnisurteil"). Multiple types can be specified as a comma-separated list or as repeated
 * parameters. - `typeGroup`: Extended filtering by type group (e.g., "Urteil", "Beschluss").
 * Multiple groups can be specified as a comma-separated list or as repeated parameters.
 *
 * <p>Conversion of Filter Parameters: - `legalEffect`: The corresponding enum value is determined
 * by mapping extended string representations like "true" or "false" to the correct enum constant. -
 * `typeGroup`: String representation of type groups is mapped to their associated enum constants
 * using case-insensitive matching.
 */
@Getter
public class CaseLawSearchParams {
  @Setter String fileNumber;
  @Setter String ecli;

  @Schema(
      description =
          "Filter by court name (Finanzgericht Münster, FG Münster, ArbG Köln) or court type (Finanzgericht, FG, ArbG). "
              + "Supports both long and short names.")
  @Setter
  String court;

  @Schema(
      name = "legalEffect",
      description =
          "Corresponds to “Rechtskraft”, meaning that the decision referred to is legally binding.")
  LegalEffect legalEffect;

  public void setLegalEffect(@NotNull String force) {
    this.legalEffect = LegalEffect.extendedValueOf(force);
  }

  @Schema(
      description =
          "Filter by document type (Urteil, Versäumnisurteil, Entscheidung etc.). "
              + "Multiple values may be specified as a comma-separated list or by repeating the parameter.",
      examples = {"Versäumnisurteil", "Entscheidung"})
  @Setter
  String[] type;

  @Schema(
      description =
          "Extended filter by type group. Multiple values may be specified as a comma-separated list or by repeating the parameter.",
      allowableValues = {"Urteil", "Beschluss", "other"})
  CaseLawDocumentTypeGroup[] typeGroup;

  public void setTypeGroup(@NotNull String[] value) {
    this.typeGroup =
        Arrays.stream(value)
            .map(CaseLawDocumentTypeGroup::extendedValueOf)
            .toArray(CaseLawDocumentTypeGroup[]::new);
  }
}
