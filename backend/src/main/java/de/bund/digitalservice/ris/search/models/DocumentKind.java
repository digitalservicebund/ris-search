package de.bund.digitalservice.ris.search.models;

import com.fasterxml.jackson.annotation.JsonValue;
import de.bund.digitalservice.ris.search.utils.eli.EliFile;
import de.bund.digitalservice.ris.search.utils.eli.ExpressionEli;
import java.util.Optional;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/** Enum for the different kinds of documents that can be searched for. */
@Getter
public enum DocumentKind {
  CASE_LAW("R", "case-law"),
  LEGISLATION("N", "norms"),
  LITERATURE("L", "literature"),
  ADMINISTRATIVE_DIRECTIVE("V", null),
  ;

  private final String singleLetterAlias;
  private final String siteMapPath;

  DocumentKind(String singleLetterAlias, String siteMapPath) {
    this.singleLetterAlias = singleLetterAlias;
    this.siteMapPath = siteMapPath;
  }

  @JsonValue
  public String getSingleLetterAlias() {
    return singleLetterAlias;
  }

  /**
   * Resolves a string documentKindString to the corresponding {@code DocumentKind} enum constant.
   * This method extends the default {@code valueOf} behavior by first checking for a match on the
   * single letter alias and then the standard enum lookup for other valid names.
   *
   * @param documentKindString the string to find the value for.
   * @return the {@code DocumentKind} corresponding to documentKindString.
   * @throws IllegalArgumentException if the input documentKindString does not correspond to any
   *     defined {@code DocumentKind} constant.
   * @throws NullPointerException if the input documentKindString is null.
   */
  public static DocumentKind valueFromString(String documentKindString) {
    return switch (documentKindString.toUpperCase()) {
      case "R" -> DocumentKind.CASE_LAW;
      case "N" -> DocumentKind.LEGISLATION;
      case "L" -> DocumentKind.LITERATURE;
      case "V" -> DocumentKind.ADMINISTRATIVE_DIRECTIVE;
      default -> DocumentKind.valueOf(documentKindString.toUpperCase());
    };
  }

  /**
   * Using the {@link Component} annotation, this converter class is registered to handle extended
   * value conversion when this enum is used as a request parameter.
   */
  @Component
  public static class FromStringConverter implements Converter<String, DocumentKind> {

    @Override
    public DocumentKind convert(@NotNull String source) {
      return DocumentKind.valueFromString(source);
    }
  }

  /**
   * Returns the id corresponding to the file name and document kinds provided.
   *
   * @param fileName the name of the file to extract the document id from
   * @param docKind the kind of document the file corresponds to
   * @return the id corresponding to the file name.
   */
  public static Optional<String> extractIdFromFileName(String fileName, DocumentKind docKind) {
    return switch (docKind) {
      case DocumentKind.LEGISLATION ->
          EliFile.fromString(fileName).map(EliFile::getExpressionEli).map(ExpressionEli::toString);
      case DocumentKind.CASE_LAW ->
          Optional.ofNullable(fileName)
              .filter(e -> e.endsWith(".xml"))
              .map(path -> path.substring(path.lastIndexOf("/") + 1, path.length() - 4));
      case DocumentKind.LITERATURE ->
          Optional.ofNullable(fileName)
              .filter(e -> e.endsWith(".akn.xml"))
              .map(path -> path.substring(path.lastIndexOf("/") + 1, path.length() - 8));
      case null, default -> Optional.empty();
    };
  }
}
