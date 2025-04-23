package de.bund.digitalservice.ris.search.api.parameters;

import de.bund.digitalservice.ris.search.caselawhandover.model.LegalEffect;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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
          "Corresponds to “Rechtskraft”, meaning that the decision referred to is legally binding.",
      allowableValues = {
        "true", "false"
      } /* accepted by {@link LegalEffect::extendedValueOf} in addition to the enum
        values */)
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
