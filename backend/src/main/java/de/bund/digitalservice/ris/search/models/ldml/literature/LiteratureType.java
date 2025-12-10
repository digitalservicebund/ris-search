package de.bund.digitalservice.ris.search.models.ldml.literature;

/** possible LiteratureTypes */
public enum LiteratureType {
  SLI,
  ULI,
  UNKNOWN;

  /**
   * Determines the literaturetype of a literature ldml based on its documentNumber
   *
   * @param documentNumber documentNumber of a literature document
   * @return {@link LiteratureType}
   */
  public static LiteratureType getByDocumentNumber(String documentNumber) {
    switch (documentNumber.substring(2, 4)) {
      case "LU" -> {
        return LiteratureType.ULI;
      }
      case "LS" -> {
        return LiteratureType.SLI;
      }
      default -> {
        return LiteratureType.UNKNOWN;
      }
    }
  }
}
