package de.bund.digitalservice.ris.search.xsd;

/**
 * The documentation of one type of the {@code ris} namespace, read from its {@code
 * xs:documentation} elements.
 *
 * @param typeName the type name, e.g. {@code ris:tatbestand}
 * @param documentationDe the German text, blank if there is none
 * @param documentationEn the English text, blank if there is none
 * @param schemaFile the schema file the type is defined in, e.g. {@code caselaw-decision.xsd}
 */
public record TypeDocumentation(
    String typeName, String documentationDe, String documentationEn, String schemaFile) {

  /**
   * Returns the text in the given language.
   *
   * @param language {@code de} or {@code en}
   * @return the text, blank if there is none in that language or the language is not supported
   */
  public String documentationIn(String language) {
    return switch (language) {
      case "de" -> documentationDe;
      case "en" -> documentationEn;
      default -> "";
    };
  }
}
