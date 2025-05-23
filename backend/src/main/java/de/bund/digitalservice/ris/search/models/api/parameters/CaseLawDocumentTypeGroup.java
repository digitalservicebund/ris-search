package de.bund.digitalservice.ris.search.models.api.parameters;

public enum CaseLawDocumentTypeGroup {
  URTEIL,
  BESCHLUSS,
  OTHER;

  public static CaseLawDocumentTypeGroup extendedValueOf(String value)
      throws IllegalArgumentException {
    return CaseLawDocumentTypeGroup.valueOf(value.toUpperCase());
  }
}
