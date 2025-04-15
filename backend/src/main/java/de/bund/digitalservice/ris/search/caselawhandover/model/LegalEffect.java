package de.bund.digitalservice.ris.search.caselawhandover.model;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import lombok.Getter;

/**
 * Corresponds to “Rechtskraft”, meaning that the {@link CaseLawDocumentationUnit} referred to is
 * legally binding.
 */
@Getter
public enum LegalEffect {
  JA,
  NEIN,
  KEINE_ANGABE,
  FALSCHE_ANGABE;

  public static LegalEffect extendedValueOf(String value) throws IllegalArgumentException {
    return switch (value) {
      case "true" -> LegalEffect.JA;
      case "false" -> LegalEffect.NEIN;
      default -> LegalEffect.valueOf(value);
    };
  }
}
