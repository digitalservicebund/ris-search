package de.bund.digitalservice.ris.search.models.api.parameters;

/**
 * This enum represents different groups of case law document types.
 *
 * <p>The available types are: - URTEIL, BESCHLUSS and OTHER.
 *
 * <p>The enum provides a method to retrieve an enum constant by its string representation, ignoring
 * case sensitivity.
 */
public enum CaseLawDocumentTypeGroup {
  URTEIL,
  BESCHLUSS,
  OTHER;

  public static CaseLawDocumentTypeGroup extendedValueOf(String value)
      throws IllegalArgumentException {
    return CaseLawDocumentTypeGroup.valueOf(value.toUpperCase());
  }
}
